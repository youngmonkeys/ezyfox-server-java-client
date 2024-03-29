package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.EzyTcpClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.constant.EzySslType;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.entity.EzyUser;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;
import com.tvd12.ezyfoxserver.client.request.EzyAppAccessRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzySocketReader;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSslSocketClient;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import java.util.Objects;
import java.util.Random;

import static org.mockito.Mockito.*;

@SuppressWarnings("resource")
public class EzyTcpClientTest extends BaseTest {

    @Test
    public void connect() {
        // given
        int reconnectPeriod = 3;
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .maxReconnectCount(0)
            .reconnectPeriod(reconnectPeriod)
            .done()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        sut.connect("unknown", 0);
    }

    @Test
    public void connectAgain() {
        // given
        int reconnectPeriod = 3;
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .maxReconnectCount(0)
            .reconnectPeriod(reconnectPeriod)
            .done()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);
        sut.setStatus(EzyConnectionStatus.CONNECTED);

        // when
        // then
        sut.connect("unknown", 0);
    }

    @Test
    public void connectError() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzySocketClient mockSocketClient = mock(EzySocketClient.class);
        String host = "host";
        int port = 0;
        doThrow(new RuntimeException("just test"))
            .when(mockSocketClient)
            .connectTo(host, port);
        EzyTcpClient sut = new EzyTcpClient(config) {
            protected EzySocketClient newSocketClient() {
                return mockSocketClient;
            }
        };
        sut.setStatus(EzyConnectionStatus.FAILURE);

        // when
        sut.connect(host, port);

        // then
        assert !sut.isConnected();
        verify(mockSocketClient, times(1)).connectTo(host, port);
    }

    @Test
    public void reconnectAble() throws Exception {
        // given
        int reconnectPeriod = 3;
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .maxReconnectCount(0)
            .reconnectPeriod(reconnectPeriod)
            .done()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        sut.connect("unknown", 0);
        Thread.sleep(500);
        sut.setStatus(EzyConnectionStatus.FAILURE);
        sut.reconnect();
    }

    @Test
    public void reconnectNotAble() {
        // given
        int reconnectPeriod = 3;
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .maxReconnectCount(0)
            .reconnectPeriod(reconnectPeriod)
            .done()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        sut.connect("unknown", 0);
        sut.reconnect();
    }

    @Test
    public void reconnectSuccess() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzySocketClient mockSocketClient = mock(EzySocketClient.class);
        when(mockSocketClient.reconnect()).thenReturn(true);
        EzyTcpClient sut = new EzyTcpClient(config) {
            protected EzySocketClient newSocketClient() {
                return mockSocketClient;
            }
        };
        sut.setStatus(EzyConnectionStatus.FAILURE);

        // when
        sut.reconnect();

        // then
        verify(mockSocketClient, times(1)).reconnect();
        assert sut.getStatus() == EzyConnectionStatus.RECONNECTING;
    }

    @Test
    public void disconnect() {
        // given
        int reconnectPeriod = 3;
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .maxReconnectCount(0)
            .reconnectPeriod(reconnectPeriod)
            .done()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        sut.disconnect(EzyDisconnectReason.CLOSE.getId());
    }

    @Test
    public void sendRequest() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        EzyCommand command = EzyCommand.APP_ACCESS;
        EzyData data = EzyEntityFactory.newArray();
        EzyRequest request = mock(EzyRequest.class);
        when(request.getCommand()).thenReturn(command);
        when(request.serialize()).thenReturn(data);

        // when
        // then
        sut.send(request);
    }

    @Test
    public void sendPingRequest() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        EzyCommand command = EzyCommand.PING;
        EzyData data = EzyEntityFactory.newArray();
        EzyRequest request = mock(EzyRequest.class);
        when(request.getCommand()).thenReturn(command);
        when(request.serialize()).thenReturn(data);

        // when
        // then
        sut.send(request);
    }

    @Test
    public void sendCommandArray() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        EzyCommand command = EzyCommand.APP_ACCESS;
        EzyArray data = EzyEntityFactory.newArray();

        // when
        // then
        sut.send(command, data);
    }

    @Test
    public void processEvents() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        sut.processEvents();
    }

    @Test
    public void close() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        sut.close();
    }

    @Test
    public void propertiesTest() {
        // given
        String clientName = "testClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        EzyZone zone = mock(EzyZone.class);
        sut.setZone(zone);

        EzyAppManager appManager = mock(EzyAppManager.class);
        when(zone.getAppManager()).thenReturn(appManager);

        int appId = new Random().nextInt();
        EzyApp app = mock(EzyApp.class);
        when(appManager.getApp()).thenReturn(app);
        when(appManager.getAppById(appId)).thenReturn(app);

        EzyUser me = mock(EzyUser.class);
        sut.setMe(me);

        sut.setStatus(EzyConnectionStatus.CONNECTED);

        long sessionId = new Random().nextLong();
        sut.setSessionId(sessionId);

        String sessionToken = "testSessionToken";
        sut.setSessionToken(sessionToken);

        byte[] publicKey = RandomUtil.randomShortByteArray();
        sut.setPublicKey(publicKey);

        byte[] privateKey = RandomUtil.randomShortByteArray();
        sut.setPrivateKey(privateKey);

        sut.setUdpStatus(EzyConnectionStatus.CONNECTING);

        EzySocketReader socketReader = mock(EzySocketReader.class);
        FieldUtil.setFieldValue(
            sut.getSocket(),
            "socketReader",
            socketReader
        );
        byte[] sessionKey = "hello".getBytes();
        sut.setSessionKey(sessionKey);

        SSLContext sslContext = mock(SSLContext.class);
        sut.setSslContext(sslContext);

        // then
        assert sut.setup() != null;
        assert sut.getName().equals(clientName);
        assert sut.getConfig() == config;
        assert sut.getZone() == zone;
        assert sut.getMe() == me;
        assert sut.getStatus() == EzyConnectionStatus.CONNECTED;
        assert sut.getSocket() != null;
        assert sut.getApp() == app;
        assert sut.getAppById(appId) == app;
        assert sut.isConnected();
        assert sut.getSessionId() == sessionId;
        assert sut.getPublicKey() == publicKey;
        assert sut.getPrivateKey() == privateKey;
        assert sut.getUdpStatus() == EzyConnectionStatus.CONNECTING;
        assert !sut.isUdpConnected();
        assert sut.getSessionKey() == sessionKey;
        assert sut.isEnableSSL() == config.isEnableSSL();
        assert sut.getSslType() == config.getSslType();
        assert sut.isEnableEncryption() == config.isEnableEncryption();
        assert sut.isEnableDebug() == config.isEnableDebug();
        assert Objects.equals(sut.getSessionToken(), sessionToken);
    }

    @Test
    public void propertiesTestWithNoZone() {
        // given
        String clientName = "testClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        sut.setStatus(EzyConnectionStatus.CONNECTED);

        long sessionId = new Random().nextLong();
        sut.setSessionId(sessionId);

        String sessionToken = "testSessionToken";
        sut.setSessionToken(sessionToken);

        // then
        assert sut.getApp() == null;
        assert sut.getAppById(0) == null;
    }

    @Test
    public void udpMethods() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        try {
            sut.udpConnect(0);
        } catch (Exception e) {
            assert e instanceof UnsupportedOperationException;
        }

        try {
            sut.udpConnect("", 0);
        } catch (Exception e) {
            assert e instanceof UnsupportedOperationException;
        }

        try {
            sut.udpSend(mock(EzyRequest.class));
        } catch (Exception e) {
            assert e instanceof UnsupportedOperationException;
        }

        try {
            sut.udpSend(EzyCommand.APP_ACCESS, null);
        } catch (Exception e) {
            assert e instanceof UnsupportedOperationException;
        }

        try {
            sut.udpSend(new EzyAppAccessRequest("app"));
        } catch (Exception e) {
            assert e instanceof UnsupportedOperationException;
        }

        try {
            sut.udpSend(
                EzyCommand.APP_REQUEST,
                EzyEntityFactory.EMPTY_ARRAY
            );
        } catch (Exception e) {
            assert e instanceof UnsupportedOperationException;
        }
    }

    @Test
    public void sendEncryptedSessionKeyNullAndDebug() {
        // given
        String clientName = "testClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .enableDebug()
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        // then
        sut.send(
            EzyCommand.APP_REQUEST,
            EzyEntityFactory.EMPTY_ARRAY,
            true
        );
    }

    @Test
    public void sendEncryptedSessionKeyNullButNotDebug() {
        // given
        String clientName = "testClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .enableDebug(false)
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.send(
                EzyCommand.APP_REQUEST,
                EzyEntityFactory.EMPTY_ARRAY,
                true
            )
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @Test
    public void sendEncryptedSessionKeyNotNull() {
        // given
        String clientName = "testClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .enableDebug(false)
            .build();
        EzyTcpClient sut = new EzyTcpClient(config);
        byte[] sessionKey = RandomUtil.randomShortByteArray();
        EzySocketReader socketReader = mock(EzySocketReader.class);
        FieldUtil.setFieldValue(
            sut.getSocket(),
            "socketReader",
            socketReader
        );
        sut.setSessionKey(sessionKey);

        // when
        sut.send(
            EzyCommand.APP_REQUEST,
            EzyEntityFactory.EMPTY_ARRAY,
            true
        );

        // then
        verify(socketReader, times(1)).setSessionKey(sessionKey);
    }

    @Test
    public void createSslSocketClientTest() {
        // given
        String clientName = "testClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .enableSSL()
            .sslType(EzySslType.CERTIFICATION)
            .build();

        // when
        EzyTcpClient sut = new EzyTcpClient(config);
        SSLContext sslContext = mock(SSLContext.class);
        sut.setSslContext(sslContext);

        // then
        Asserts.assertEqualsType(
            sut.getSocket(),
            EzyTcpSslSocketClient.class
        );
    }
}
