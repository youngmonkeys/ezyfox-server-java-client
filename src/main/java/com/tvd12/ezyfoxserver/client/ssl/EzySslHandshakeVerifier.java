package com.tvd12.ezyfoxserver.client.ssl;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class EzySslHandshakeVerifier {

    public static final int PROOF_VERSION = 1;
    public static final String SIGNATURE_DOMAIN = "ezyfox-handshake-v1";
    public static final int MAX_FIELD_BYTES = 0xFFFF;
    public static final int MAX_CERTIFICATES = 8;
    protected static final String AUTH_TYPE = "UNKNOWN";
    protected static final int SAN_DNS_NAME = 2;
    protected static final int SAN_IP_ADDRESS = 7;

    protected final X509TrustManager trustManager;

    public EzySslHandshakeVerifier() {
        this(null);
    }

    public EzySslHandshakeVerifier(X509TrustManager trustManager) {
        this.trustManager = trustManager != null
            ? trustManager
            : defaultTrustManager();
    }

    public X509Certificate verify(
        byte[] proof,
        byte[] clientKey,
        byte[] encryptedSessionKey,
        String hostName
    ) {
        if (proof == null || proof.length == 0) {
            throw new EzySslHandshakeVerificationException(
                "server did not sign the handshake, " +
                    "maybe server does not enable ssl handshake signing"
            );
        }
        if (clientKey == null || clientKey.length == 0) {
            throw new EzySslHandshakeVerificationException("client key is empty");
        }
        if (encryptedSessionKey == null || encryptedSessionKey.length == 0) {
            throw new EzySslHandshakeVerificationException("session key is empty");
        }
        Proof parsedProof = parseProof(proof);
        X509Certificate[] chain = parsedProof.certificates;
        try {
            trustManager.checkServerTrusted(chain, AUTH_TYPE);
        } catch (Exception e) {
            throw new EzySslHandshakeVerificationException(
                "server certificate is not trusted: " + e.getMessage(),
                e
            );
        }
        X509Certificate leaf = chain[0];
        if (hostName != null && !matchesHostName(leaf, hostName)) {
            throw new EzySslHandshakeVerificationException(
                "server certificate: " + leaf.getSubjectX500Principal() +
                    " does not match host name: " + hostName
            );
        }
        if (!verifySignature(leaf, parsedProof.signature, clientKey, encryptedSessionKey)) {
            throw new EzySslHandshakeVerificationException(
                "handshake signature is invalid"
            );
        }
        return leaf;
    }

    public static byte[] signedMessage(byte[] clientKey, byte[] encryptedSessionKey) {
        byte[] domain = SIGNATURE_DOMAIN.getBytes(StandardCharsets.US_ASCII);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(domain, 0, domain.length);
        writeField(out, clientKey);
        writeField(out, encryptedSessionKey);
        return out.toByteArray();
    }

    protected static void writeField(ByteArrayOutputStream out, byte[] value) {
        if (value.length > MAX_FIELD_BYTES) {
            throw new IllegalArgumentException("field too large: " + value.length);
        }
        out.write(value.length >>> 8);
        out.write(value.length & 0xFF);
        out.write(value, 0, value.length);
    }

    protected Proof parseProof(byte[] proof) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(proof));
            int version = in.readUnsignedByte();
            if (version != PROOF_VERSION) {
                throw new EzySslHandshakeVerificationException(
                    "unsupported handshake proof version: " + version
                );
            }
            byte[] signature = readField(in);
            int count = in.readUnsignedByte();
            if (count == 0 || count > MAX_CERTIFICATES) {
                throw new EzySslHandshakeVerificationException(
                    "invalid handshake proof certificate count: " + count
                );
            }
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate[] certificates = new X509Certificate[count];
            for (int i = 0; i < count; ++i) {
                certificates[i] = (X509Certificate) factory.generateCertificate(
                    new ByteArrayInputStream(readField(in))
                );
            }
            if (in.available() != 0) {
                throw new EzySslHandshakeVerificationException(
                    "handshake proof has trailing bytes"
                );
            }
            return new Proof(signature, certificates);
        } catch (EzySslHandshakeVerificationException e) {
            throw e;
        } catch (Exception e) {
            throw new EzySslHandshakeVerificationException(
                "invalid handshake proof: " + e.getMessage(),
                e
            );
        }
    }

    protected static byte[] readField(DataInputStream in) throws IOException {
        byte[] value = new byte[in.readUnsignedShort()];
        in.readFully(value);
        return value;
    }

    protected boolean verifySignature(
        X509Certificate certificate,
        byte[] signatureBytes,
        byte[] clientKey,
        byte[] encryptedSessionKey
    ) {
        PublicKey publicKey = certificate.getPublicKey();
        String algorithm;
        if ("RSA".equals(publicKey.getAlgorithm())) {
            algorithm = "SHA256withRSA";
        } else if ("EC".equals(publicKey.getAlgorithm())) {
            algorithm = "SHA256withECDSA";
        } else {
            throw new EzySslHandshakeVerificationException(
                "unsupported server key algorithm: " + publicKey.getAlgorithm()
            );
        }
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(signedMessage(clientKey, encryptedSessionKey));
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean matchesHostName(X509Certificate certificate, String hostName) {
        String host = normalizeHostName(hostName);
        boolean ipAddress = isIpAddress(host);
        Collection<List<?>> names;
        try {
            names = certificate.getSubjectAlternativeNames();
        } catch (Exception e) {
            return false;
        }
        if (names == null) {
            return false;
        }
        for (List<?> name : names) {
            if (name.size() < 2) {
                continue;
            }
            int type = (Integer) name.get(0);
            String value = String.valueOf(name.get(1));
            if (ipAddress && type == SAN_IP_ADDRESS && sameIpAddress(host, value)) {
                return true;
            }
            if (!ipAddress && type == SAN_DNS_NAME && matchesDnsName(host, value)) {
                return true;
            }
        }
        return false;
    }

    protected static String normalizeHostName(String hostName) {
        String host = hostName.trim().toLowerCase(Locale.ENGLISH);
        if (host.startsWith("[") && host.endsWith("]")) {
            host = host.substring(1, host.length() - 1);
        }
        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        return host;
    }

    protected static boolean matchesDnsName(String host, String dnsName) {
        String pattern = normalizeHostName(dnsName);
        if (!pattern.startsWith("*.")) {
            return host.equals(pattern);
        }
        String suffix = pattern.substring(1);
        if (!host.endsWith(suffix)) {
            return false;
        }
        String label = host.substring(0, host.length() - suffix.length());
        return !label.isEmpty() && label.indexOf('.') < 0;
    }

    protected static boolean isIpAddress(String host) {
        return host.indexOf(':') >= 0 || host.matches("[0-9.]+");
    }

    protected static boolean sameIpAddress(String host, String value) {
        try {
            return InetAddress.getByName(host).equals(InetAddress.getByName(value));
        } catch (Exception e) {
            return false;
        }
    }

    protected static X509TrustManager defaultTrustManager() {
        try {
            TrustManagerFactory factory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            );
            factory.init((KeyStore) null);
            for (TrustManager trustManager : factory.getTrustManagers()) {
                if (trustManager instanceof X509TrustManager) {
                    return (X509TrustManager) trustManager;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("can not load default trust manager", e);
        }
        throw new IllegalStateException("there is no default x509 trust manager");
    }

    protected static class Proof {
        protected final byte[] signature;
        protected final X509Certificate[] certificates;

        protected Proof(byte[] signature, X509Certificate[] certificates) {
            this.signature = signature;
            this.certificates = certificates;
        }
    }
}
