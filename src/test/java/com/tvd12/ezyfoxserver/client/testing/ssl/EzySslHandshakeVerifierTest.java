package com.tvd12.ezyfoxserver.client.testing.ssl;

import com.tvd12.ezyfox.security.EzyKeysGenerator;
import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerificationException;
import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerifier;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.tvd12.ezyfoxserver.client.testing.ssl.EzySslHandshakeTestSigner.EC_KEYSTORE;
import static com.tvd12.ezyfoxserver.client.testing.ssl.EzySslHandshakeTestSigner.HOST_NAME;
import static com.tvd12.ezyfoxserver.client.testing.ssl.EzySslHandshakeTestSigner.RSA_KEYSTORE;

public class EzySslHandshakeVerifierTest {

    private final byte[] clientKey = EzyKeysGenerator.builder()
        .build()
        .generate()
        .getPublic()
        .getEncoded();
    private final byte[] encryptedSessionKey = new byte[] {1, 2, 3, 4, 5, 6, 7, 8};

    @Test
    public void verifyRsaTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);

        // when
        X509Certificate actual = sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME);

        // then
        Asserts.assertEquals(actual.getPublicKey().getAlgorithm(), "RSA");
    }

    @Test
    public void verifyEcTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(EC_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);

        // when
        X509Certificate actual = sut.verify(proof, clientKey, encryptedSessionKey, "LOCALHOST.");

        // then
        Asserts.assertEquals(actual.getPublicKey().getAlgorithm(), "EC");
    }

    @Test
    public void verifyTamperedSessionKeyTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);
        byte[] tamperedKey = Arrays.copyOf(encryptedSessionKey, encryptedSessionKey.length);
        tamperedKey[0] ^= 0x01;

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, tamperedKey, HOST_NAME)
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        Asserts.assertEquals(e.getMessage(), "handshake signature is invalid");
    }

    @Test
    public void verifyOtherClientKeyTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(EC_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);
        byte[] otherClientKey = Arrays.copyOf(clientKey, clientKey.length);
        otherClientKey[otherClientKey.length - 1] ^= 0x01;

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, otherClientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
    }

    @Test
    public void verifyWrongHostNameTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, "evil.com")
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        Asserts.assertTrue(e.getMessage().contains("does not match host name"));
    }

    @Test
    public void verifyNullHostNameSkipHostCheckTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);

        // when
        X509Certificate actual = sut.verify(proof, clientKey, encryptedSessionKey, null);

        // then
        Asserts.assertNotNull(actual);
    }

    @Test
    public void verifyUntrustedCertificateTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier();
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        Asserts.assertTrue(e.getMessage().startsWith("server certificate is not trusted"));
    }

    @Test
    public void verifyEmptyProofTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());

        // when
        Throwable nullProof = Asserts.assertThrows(() ->
            sut.verify(null, clientKey, encryptedSessionKey, HOST_NAME)
        );
        Throwable emptyProof = Asserts.assertThrows(() ->
            sut.verify(new byte[0], clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEqualsType(nullProof, EzySslHandshakeVerificationException.class);
        Asserts.assertEqualsType(emptyProof, EzySslHandshakeVerificationException.class);
    }

    @Test
    public void verifyEmptyKeysTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.sign(clientKey, encryptedSessionKey);

        // when
        Throwable noClientKey = Asserts.assertThrows(() ->
            sut.verify(proof, null, encryptedSessionKey, HOST_NAME)
        );
        Throwable noSessionKey = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, new byte[0], HOST_NAME)
        );

        // then
        Asserts.assertEquals(noClientKey.getMessage(), "client key is empty");
        Asserts.assertEquals(noSessionKey.getMessage(), "session key is empty");
    }

    @Test
    public void verifyUnsupportedVersionTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.newProof(2, signer.signature(clientKey, encryptedSessionKey), 1);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEquals(e.getMessage(), "unsupported handshake proof version: 2");
    }

    @Test
    public void verifyNoCertificateTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.newProof(1, signer.signature(clientKey, encryptedSessionKey), 0);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEquals(e.getMessage(), "invalid handshake proof certificate count: 0");
    }

    @Test
    public void verifyTrailingBytesTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(signer.sign(clientKey, encryptedSessionKey));
        out.write(0);
        byte[] proof = out.toByteArray();

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEquals(e.getMessage(), "handshake proof has trailing bytes");
    }

    @Test
    public void verifyTruncatedProofTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] fullProof = signer.sign(clientKey, encryptedSessionKey);
        byte[] proof = Arrays.copyOf(fullProof, fullProof.length / 2);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        Asserts.assertTrue(e.getMessage().startsWith("invalid handshake proof"));
    }

    @Test
    public void matchesDnsNameTest() {
        Asserts.assertTrue(DnsNameMatcher.matches("api.example.com", "*.example.com"));
        Asserts.assertTrue(DnsNameMatcher.matches("example.com", "EXAMPLE.COM."));
        Asserts.assertFalse(DnsNameMatcher.matches("example.com", "*.example.com"));
        Asserts.assertFalse(DnsNameMatcher.matches("a.b.example.com", "*.example.com"));
        Asserts.assertFalse(DnsNameMatcher.matches("evilexample.com", "*.example.com"));
    }

    @Test
    public void signedMessageFieldTooLargeTest() {
        // given
        byte[] tooLargeKey = new byte[EzySslHandshakeVerifier.MAX_FIELD_BYTES + 1];

        // when
        Throwable e = Asserts.assertThrows(() ->
            EzySslHandshakeVerifier.signedMessage(tooLargeKey, encryptedSessionKey)
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
        Asserts.assertEquals(e.getMessage(), "field too large: " + tooLargeKey.length);
    }

    @Test
    public void verifyTooManyCertificatesTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(RSA_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        int count = EzySslHandshakeVerifier.MAX_CERTIFICATES + 1;
        byte[] proof = signer.newProof(1, signer.signature(clientKey, encryptedSessionKey), count);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEquals(e.getMessage(), "invalid handshake proof certificate count: " + count);
    }

    @Test
    public void verifyMalformedSignatureTest() throws Exception {
        // given
        EzySslHandshakeTestSigner signer = new EzySslHandshakeTestSigner(EC_KEYSTORE);
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(signer.trustManager());
        byte[] proof = signer.newProof(1, new byte[]{1, 2, 3}, 1);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verify(proof, clientKey, encryptedSessionKey, HOST_NAME)
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        Asserts.assertEquals(e.getMessage(), "handshake signature is invalid");
    }

    @Test
    public void verifySignatureUnsupportedKeyAlgorithmTest() {
        // given
        TestVerifier sut = new TestVerifier();
        PublicKey publicKey = mock(PublicKey.class);
        when(publicKey.getAlgorithm()).thenReturn("DSA");
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getPublicKey()).thenReturn(publicKey);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.verifySignature0(certificate, new byte[]{1}, clientKey, encryptedSessionKey)
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        Asserts.assertEquals(e.getMessage(), "unsupported server key algorithm: DSA");
    }

    @Test
    public void matchesHostNameWhenGetNamesErrorTest() throws Exception {
        // given
        TestVerifier sut = new TestVerifier();
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getSubjectAlternativeNames())
            .thenThrow(new CertificateParsingException("test"));

        // when
        // then
        Asserts.assertFalse(sut.matchesHostName0(certificate, HOST_NAME));
    }

    @Test
    public void matchesHostNameWhenNoNamesTest() throws Exception {
        // given
        TestVerifier sut = new TestVerifier();
        X509Certificate certificate = mock(X509Certificate.class);
        when(certificate.getSubjectAlternativeNames()).thenReturn(null);

        // when
        // then
        Asserts.assertFalse(sut.matchesHostName0(certificate, HOST_NAME));
    }

    @Test
    public void matchesHostNameWithIpAddressTest() throws Exception {
        // given
        TestVerifier sut = new TestVerifier();
        X509Certificate certificate = mock(X509Certificate.class);
        Collection<List<?>> names = Arrays.asList(
            Collections.singletonList(2),
            Arrays.asList(2, "127.0.0.1"),
            Arrays.asList(7, "[::zz]"),
            Arrays.asList(7, "127.0.0.1"),
            Arrays.asList(7, "0:0:0:0:0:0:0:1")
        );
        when(certificate.getSubjectAlternativeNames()).thenReturn(names);

        // when
        // then
        Asserts.assertTrue(sut.matchesHostName0(certificate, "127.0.0.1"));
        Asserts.assertTrue(sut.matchesHostName0(certificate, "[::1]"));
        Asserts.assertFalse(sut.matchesHostName0(certificate, "127.0.0.2"));
        Asserts.assertFalse(sut.matchesHostName0(certificate, "localhost"));
    }

    @Test
    public void matchesHostNameWithDnsNameTest() throws Exception {
        // given
        TestVerifier sut = new TestVerifier();
        X509Certificate certificate = mock(X509Certificate.class);
        Collection<List<?>> names = Arrays.asList(
            Arrays.asList(7, "127.0.0.1"),
            Arrays.asList(2, "*.example.com")
        );
        when(certificate.getSubjectAlternativeNames()).thenReturn(names);

        // when
        // then
        Asserts.assertTrue(sut.matchesHostName0(certificate, " API.Example.com. "));
        Asserts.assertFalse(sut.matchesHostName0(certificate, "example.com"));
        Asserts.assertFalse(sut.matchesHostName0(certificate, "127.0.0.2"));
    }

    @Test
    public void defaultTrustManagerTest() {
        // given
        // when
        EzySslHandshakeVerifier sut = new EzySslHandshakeVerifier(null);

        // then
        Asserts.assertNotNull(FieldUtil.getFieldValue(sut, "trustManager"));
    }

    private static class TestVerifier extends EzySslHandshakeVerifier {

        private TestVerifier() {
            super(mock(X509TrustManager.class));
        }

        private boolean verifySignature0(
            X509Certificate certificate,
            byte[] signatureBytes,
            byte[] clientKey,
            byte[] encryptedSessionKey
        ) {
            return verifySignature(certificate, signatureBytes, clientKey, encryptedSessionKey);
        }

        private boolean matchesHostName0(X509Certificate certificate, String hostName) {
            return matchesHostName(certificate, hostName);
        }
    }

    private static class DnsNameMatcher extends EzySslHandshakeVerifier {

        private DnsNameMatcher() {
            super(null);
        }

        private static boolean matches(String host, String dnsName) {
            return matchesDnsName(host, dnsName);
        }
    }
}
