package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyAsyCrypt;
import com.tvd12.ezyfox.security.EzyKeysGenerator;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.handler.EzyHandshakeHandler;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;
import com.tvd12.test.assertion.Asserts;
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
}
