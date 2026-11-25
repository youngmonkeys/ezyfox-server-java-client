package com.tvd12.ezyfoxserver.client.testing.ssl;

import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerifier;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;

public class EzySslHandshakeTestSigner {

    public static final String RSA_KEYSTORE = "/ssl-handshake-keystore.jks";
    public static final String EC_KEYSTORE = "/ssl-handshake-ec-keystore.jks";
    public static final String HOST_NAME = "localhost";
    private static final char[] PASSWORD = "123456".toCharArray();
    private static final String ALIAS = "handshake";

    private final KeyStore keyStore;
    private final PrivateKey privateKey;
    private final Certificate[] chain;

    public EzySslHandshakeTestSigner(String keyStoreFile) throws Exception {
        keyStore = KeyStore.getInstance("JKS");
        try (InputStream stream = getClass().getResourceAsStream(keyStoreFile)) {
            keyStore.load(stream, PASSWORD);
        }
        privateKey = (PrivateKey) keyStore.getKey(ALIAS, PASSWORD);
        chain = keyStore.getCertificateChain(ALIAS);
    }

    public byte[] sign(byte[] clientKey, byte[] encryptedSessionKey) throws Exception {
        return newProof(
            EzySslHandshakeVerifier.PROOF_VERSION,
            signature(clientKey, encryptedSessionKey),
            chain.length
        );
    }

    public byte[] signature(byte[] clientKey, byte[] encryptedSessionKey) throws Exception {
        String algorithm = "RSA".equals(privateKey.getAlgorithm())
            ? "SHA256withRSA"
            : "SHA256withECDSA";
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(EzySslHandshakeVerifier.signedMessage(clientKey, encryptedSessionKey));
        return signature.sign();
    }

    public byte[] newProof(int version, byte[] signature, int certificateCount) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(version);
        writeField(out, signature);
        out.write(certificateCount);
        for (int i = 0; i < certificateCount && i < chain.length; ++i) {
            writeField(out, chain[i].getEncoded());
        }
        return out.toByteArray();
    }

    public X509TrustManager trustManager() throws Exception {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        );
        factory.init(keyStore);
        for (TrustManager trustManager : factory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new IllegalStateException("no x509 trust manager");
    }

    private static void writeField(ByteArrayOutputStream out, byte[] value) {
        out.write(value.length >>> 8);
        out.write(value.length & 0xFF);
        out.write(value, 0, value.length);
    }
}
