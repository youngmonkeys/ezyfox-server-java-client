package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzySocketReader;
import com.tvd12.ezyfoxserver.client.socket.EzySocketResponseApi;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSslSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketClient;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class EzyUTSslSocketClientTest {

    @Test
    public void setCompressionEnableTest() {
        // given
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();

        // when
        sut.setCompressionEnable(true);

        // then
        verify(sut.udpClient(), times(1)).setCompressionEnable(true);
    }

    @Test
    public void udpClientUseCompressionThresholdTest() {
        // given
        int compressionThreshold = 1024;
        EzyClientConfig config = EzyClientConfig.builder()
            .compressionThreshold(compressionThreshold)
            .build();

        // when
        EzyUTSslSocketClient sut = new EzyUTSslSocketClient(config);

        // then
        EzyUdpSocketClient udpClient = FieldUtil.getFieldValue(sut, "udpClient");
        EzySocketResponseApi responseApi = FieldUtil.getFieldValue(udpClient, "responseApi");
        Asserts.assertEquals(
            FieldUtil.getFieldValue(responseApi, "compressionThreshold"),
            compressionThreshold
        );
    }

    @Test
    public void udpConnectWithPortTest() {
        // given
        String host = "localhost";
        int port = 2611;
        long sessionId = 1L;
        String sessionToken = "testSessionToken";
        byte[] sessionKey = new byte[]{1, 2, 3};
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();
        FieldUtil.setFieldValue(sut, "host", host);
        FieldUtil.setFieldValue(sut, "sessionKey", sessionKey);
        sut.setSessionId(sessionId);
        sut.setSessionToken(sessionToken);

        // when
        sut.udpConnect(port);

        // then
        EzyUdpSocketClient udpClient = sut.udpClient();
        verify(udpClient, times(1)).setSessionId(sessionId);
        verify(udpClient, times(1)).setSessionToken(sessionToken);
        verify(udpClient, times(1)).setSessionKey(sessionKey);
        verify(udpClient, times(1)).connectTo(host, port);
    }

    @Test
    public void udpSendMessageTest() {
        // given
        EzyArray message = EzyEntityArrays.newArray("test");
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();

        // when
        sut.udpSendMessage(message, false);

        // then
        verify(sut.udpClient(), times(1)).sendMessage(message, false);
    }

    @Test
    public void udpSetStatusTest() {
        // given
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();

        // when
        sut.udpSetStatus(EzySocketStatus.CONNECTING);

        // then
        verify(sut.udpClient(), times(1)).setStatus(EzySocketStatus.CONNECTING);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void popReadMessagesTest() {
        // given
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();
        EzySocketReader socketReader = mock(EzySocketReader.class);
        FieldUtil.setFieldValue(sut, "socketReader", socketReader);

        // when
        sut.popMessages();

        // then
        verify(socketReader, times(1)).popMessages(any(List.class));
        verify(sut.udpClient(), times(1)).popReadMessages(any(List.class));
    }

    @Test
    public void clearComponentsTest() {
        // given
        int reason = EzyDisconnectReason.CLOSE.getId();
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();

        // when
        sut.clear(reason);

        // then
        verify(sut.udpClient(), times(1)).disconnect(reason);
    }

    @Test
    public void disconnectTest() {
        // given
        int reason = EzyDisconnectReason.CLOSE.getId();
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();

        // when
        sut.disconnect(reason);

        // then
        verify(sut.udpClient(), times(1)).disconnect(reason);
    }

    @Test
    public void closeTest() {
        // given
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        TestEzyUTSslSocketClient sut = new TestEzyUTSslSocketClient();
        sut.setPingSchedule(pingSchedule);

        // when
        sut.close();

        // then
        verify(pingSchedule, times(1)).shutdown();
        verify(sut.udpClient(), times(1)).close();
    }

    private static class TestEzyUTSslSocketClient extends EzyUTSslSocketClient {

        private TestEzyUTSslSocketClient() {
            super(EzyClientConfig.builder().build());
            FieldUtil.setFieldValue(this, "udpClient", mock(EzyUdpSocketClient.class));
        }

        private EzyUdpSocketClient udpClient() {
            return udpClient;
        }

        private void popMessages() {
            popReadMessages();
        }

        private void clear(int reason) {
            clearComponents(reason);
        }
    }
}
