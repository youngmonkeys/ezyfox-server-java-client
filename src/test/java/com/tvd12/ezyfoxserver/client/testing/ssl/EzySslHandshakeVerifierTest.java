package com.tvd12.ezyfoxserver.client.testing.ssl;

import com.tvd12.ezyfox.security.EzyKeysGenerator;
import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerificationException;
import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerifier;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.security.cert.X509Certificate;
import java.util.Arrays;

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

    private static class DnsNameMatcher extends EzySslHandshakeVerifier {

        private DnsNameMatcher() {
            super(null);
        }

        private static boolean matches(String host, String dnsName) {
            return matchesDnsName(host, dnsName);
        }
    }
}
