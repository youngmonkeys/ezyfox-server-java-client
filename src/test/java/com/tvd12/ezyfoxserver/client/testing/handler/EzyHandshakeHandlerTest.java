package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyAsyCrypt;
import com.tvd12.ezyfox.security.EzyKeysGenerator;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.handler.EzyHandshakeHandler;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzySocketResponseApi;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketClient;
import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerificationException;
import com.tvd12.ezyfoxserver.client.testing.ssl.EzySslHandshakeTestSigner;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.util.Random;

import static org.mockito.Mockito.*;

public class EzyHandshakeHandlerTest {

    @Test
    public void handle() {
        // given
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        EzyArray data = EzyEntityArrays.newArray(
            "",
            sessionToken,
            sessionId

        );
        EzyClient client = mock(EzyClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyRequest loginRequest = mock(EzyRequest.class);
        EzyHandshakeHandler sut = new EzyHandshakeHandler() {
            protected EzyRequest getLoginRequest() {
                return loginRequest;
            }
        };
        sut.setClient(client);
        sut.setPingSchedule(pingSchedule);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).send(loginRequest, false);
    }

    @Test
    public void handleWhenEnableEncryption() throws Exception {
        // given
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        byte[] sessionKey = EzyAesCrypt.randomKey();
        KeyPair keyPair = EzyKeysGenerator.builder()
            .build()
            .generate();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] encryptedSessionKey = EzyAsyCrypt.builder()
            .publicKey(publicKey)
            .build()
            .encrypt(sessionKey);
        EzyArray data = EzyEntityArrays.newArray(
            "",
            sessionToken,
            sessionId,
            encryptedSessionKey

        );
        EzyClient client = mock(EzyClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        when(client.isEnableEncryption()).thenReturn(true);
        when(client.getPrivateKey()).thenReturn(privateKey);
        when(client.getPublicKey()).thenReturn(publicKey);

        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyRequest loginRequest = mock(EzyRequest.class);
        EzyHandshakeHandler sut = new EzyHandshakeHandler() {
            protected EzyRequest getLoginRequest() {
                return loginRequest;
            }
        };
        sut.setClient(client);
        sut.setPingSchedule(pingSchedule);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).send(loginRequest, true);
        verify(client, times(1)).setSessionKey(sessionKey);
    }

    @Test
    public void handleWhenEnableEncryptionDecryptSessionKeyFailed() throws Exception {
        // given
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        byte[] sessionKey = EzyAesCrypt.randomKey();
        KeyPair keyPair = EzyKeysGenerator.builder()
            .build()
            .generate();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] encryptedSessionKey = EzyAsyCrypt.builder()
            .publicKey(publicKey)
            .build()
            .encrypt(sessionKey);
        EzyArray data = EzyEntityArrays.newArray(
            "",
            sessionToken,
            sessionId,
            encryptedSessionKey

        );
        EzyClient client = mock(EzyClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        when(client.isEnableEncryption()).thenReturn(true);
        when(client.getPublicKey()).thenReturn(publicKey);

        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyRequest loginRequest = mock(EzyRequest.class);
        EzyHandshakeHandler sut = new EzyHandshakeHandler() {
            protected EzyRequest getLoginRequest() {
                return loginRequest;
            }
        };
        sut.setClient(client);
        sut.setPingSchedule(pingSchedule);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.handle(data)
        );

        // then
        Asserts.assertEqualsType(e, IllegalStateException.class);
    }

    @Test
    public void handleWhenEnableEncryptionFailedNoDebug() throws Exception {
        // given
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        EzyArray data = EzyEntityArrays.newArray(
            "",
            sessionToken,
            sessionId
        );
        EzyClient client = mock(EzyClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        when(client.isEnableEncryption()).thenReturn(true);

        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyRequest loginRequest = mock(EzyRequest.class);
        EzyHandshakeHandler sut = new EzyHandshakeHandler() {
            protected EzyRequest getLoginRequest() {
                return loginRequest;
            }
        };
        sut.setClient(client);
        sut.setPingSchedule(pingSchedule);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.handle(data)
        );

        // then
        Asserts.assertEqualsType(e, IllegalStateException.class);
    }

    @Test
    public void handleWhenEnableEncryptionFailedDebug() {
        // given
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        EzyArray data = EzyEntityArrays.newArray(
            "",
            sessionToken,
            sessionId

        );
        EzyClient client = mock(EzyClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        when(client.isEnableDebug()).thenReturn(true);
        when(client.isEnableEncryption()).thenReturn(true);
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyRequest loginRequest = mock(EzyRequest.class);
        EzyHandshakeHandler sut = new EzyHandshakeHandler() {
            protected EzyRequest getLoginRequest() {
                return loginRequest;
            }
        };
        sut.setClient(client);
        sut.setPingSchedule(pingSchedule);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).send(loginRequest, true);
    }

    @Test
    public void handleWhenVerifySslHandshakeSuccess() throws Exception {
        // given
        HandshakeContext context = new HandshakeContext(true, "localhost");
        byte[] proof = context.signer.sign(context.publicKey, context.encryptedSessionKey);

        // when
        context.sut.handle(context.newData(proof));

        // then
        verify(context.client, times(1)).send(context.loginRequest, true);
        verify(context.client, times(1)).setSessionKey(context.sessionKey);
        verify(context.client, times(0)).close();
    }

    @Test
    public void handleWhenVerifySslHandshakeUseConnectedHost() throws Exception {
        // given
        HandshakeContext context = new HandshakeContext(true, null);
        EzyUTSocketClient socketClient = mock(EzyUTSocketClient.class);
        when(socketClient.getHost()).thenReturn("localhost");
        when(context.client.getSocket()).thenReturn(socketClient);
        byte[] proof = context.signer.sign(context.publicKey, context.encryptedSessionKey);

        // when
        context.sut.handle(context.newData(proof));

        // then
        verify(context.client, times(1)).send(context.loginRequest, true);
    }

    @Test
    public void handleWhenVerifySslHandshakeNoProof() throws Exception {
        // given
        HandshakeContext context = new HandshakeContext(true, "localhost");

        // when
        Throwable e = Asserts.assertThrows(() ->
            context.sut.handle(context.newData(null))
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        verify(context.client, times(1)).close();
        verify(context.client, times(0)).setSessionKey(any());
        verify(context.client, times(0)).send(any(EzyRequest.class), anyBoolean());
    }

    @Test
    public void handleWhenVerifySslHandshakeWrongHost() throws Exception {
        // given
        HandshakeContext context = new HandshakeContext(true, "evil.com");
        byte[] proof = context.signer.sign(context.publicKey, context.encryptedSessionKey);

        // when
        Throwable e = Asserts.assertThrows(() ->
            context.sut.handle(context.newData(proof))
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        verify(context.client, times(1)).close();
        verify(context.client, times(0)).send(any(EzyRequest.class), anyBoolean());
    }

    @Test
    public void handleWhenVerifySslHandshakeUnknownHost() throws Exception {
        // given
        HandshakeContext context = new HandshakeContext(true, null);
        when(context.client.getSocket()).thenReturn(null);
        byte[] proof = context.signer.sign(context.publicKey, context.encryptedSessionKey);

        // when
        Throwable e = Asserts.assertThrows(() ->
            context.sut.handle(context.newData(proof))
        );

        // then
        Asserts.assertEqualsType(e, EzySslHandshakeVerificationException.class);
        verify(context.client, times(1)).close();
    }

    @Test
    public void handleWhenNotVerifySslHandshakeNoProof() throws Exception {
        // given
        HandshakeContext context = new HandshakeContext(false, null);

        // when
        context.sut.handle(context.newData(""));

        // then
        verify(context.client, times(1)).send(context.loginRequest, true);
        verify(context.client, times(1)).setSessionKey(context.sessionKey);
    }

    private static class HandshakeContext {
        private final EzySslHandshakeTestSigner signer;
        private final byte[] sessionKey;
        private final byte[] publicKey;
        private final byte[] encryptedSessionKey;
        private final EzyClient client;
        private final EzyRequest loginRequest;
        private final EzyHandshakeHandler sut;

        private HandshakeContext(boolean verify, String hostName) throws Exception {
            signer = new EzySslHandshakeTestSigner(EzySslHandshakeTestSigner.RSA_KEYSTORE);
            sessionKey = EzyAesCrypt.randomKey();
            KeyPair keyPair = EzyKeysGenerator.builder()
                .build()
                .generate();
            publicKey = keyPair.getPublic().getEncoded();
            encryptedSessionKey = EzyAsyCrypt.builder()
                .publicKey(publicKey)
                .build()
                .encrypt(sessionKey);
            EzyClientConfig config = EzyClientConfig.builder()
                .enableSSL()
                .verifySslHandshake(verify)
                .sslHandshakeHostName(hostName)
                .sslHandshakeTrustManager(signer.trustManager())
                .build();
            client = mock(EzyClient.class);
            when(client.getConfig()).thenReturn(config);
            when(client.getSocket()).thenReturn(new EzyUTSocketClient(config));
            when(client.isEnableEncryption()).thenReturn(true);
            when(client.getPrivateKey()).thenReturn(keyPair.getPrivate().getEncoded());
            when(client.getPublicKey()).thenReturn(publicKey);
            loginRequest = mock(EzyRequest.class);
            sut = new EzyHandshakeHandler() {
                protected EzyRequest getLoginRequest() {
                    return loginRequest;
                }
            };
            sut.setClient(client);
            sut.setPingSchedule(mock(EzyPingSchedule.class));
        }

        private EzyArray newData(Object proof) {
            return EzyEntityArrays.newArray(
                proof,
                "testSessionToken",
                new Random().nextLong(),
                encryptedSessionKey
            );
        }
    }

    @Test
    public void handleCompressionEnable() {
        // given
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        EzyArray data = EzyEntityArrays.newArray(
            "",
            "testSessionToken",
            1L,
            null,
            true
        );
        EzyClient client = mock(EzyClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyRequest loginRequest = mock(EzyRequest.class);
        EzyHandshakeHandler sut = new EzyHandshakeHandler() {
            protected EzyRequest getLoginRequest() {
                return loginRequest;
            }
        };
        sut.setClient(client);
        sut.setPingSchedule(pingSchedule);

        // when
        sut.handle(data);

        // then
        EzySocketResponseApi responseApi = FieldUtil.getFieldValue(socketClient, "responseApi");
        EzyUdpSocketClient udpClient = FieldUtil.getFieldValue(socketClient, "udpClient");
        EzySocketResponseApi udpResponseApi = FieldUtil.getFieldValue(udpClient, "responseApi");
        Asserts.assertTrue(responseApi.isCompressionEnable());
        Asserts.assertTrue(udpResponseApi.isCompressionEnable());
    }
}
