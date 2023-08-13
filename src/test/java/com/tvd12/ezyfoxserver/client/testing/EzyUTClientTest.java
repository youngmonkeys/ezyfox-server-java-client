package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.*;

@SuppressWarnings("resource")
public class EzyUTClientTest extends BaseTest {

    @Test
    public void creation() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();

        // when
        // then
        new EzyUTClient(config);
    }

    @Test
    public void udpConnectHostPort() {
        // given
        String host = "host";
        int port = new Random().nextInt();
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyUTSocketClient mockSocketClient = mock(EzyUTSocketClient.class);
        EzyUTClient sut = new EzyUTClient(config) {
            protected EzyTcpSocketClient newTcpSocketClient(EzySocketClientConfig config) {
                return mockSocketClient;
            }
        };

        // when
        sut.udpConnect(host, port);

        // then
        verify(mockSocketClient, times(1)).udpConnect(host, port);
    }

    @Test
    public void udpConnectPort() {
        // given
        int port = new Random().nextInt();
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyUTSocketClient mockSocketClient = mock(EzyUTSocketClient.class);
        EzyUTClient sut = new EzyUTClient(config) {
            protected EzyTcpSocketClient newTcpSocketClient(EzySocketClientConfig config) {
                return mockSocketClient;
            }
        };

        // when
        sut.udpConnect(port);

        // then
        verify(mockSocketClient, times(1)).udpConnect(port);
    }

    @Test
    public void updSendRequest() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyUTSocketClient mockSocketClient = mock(EzyUTSocketClient.class);
        EzyUTClient sut = new EzyUTClient(config) {
            protected EzyTcpSocketClient newTcpSocketClient(EzySocketClientConfig config) {
                return mockSocketClient;
            }
        };
        EzyCommand cmd = EzyCommand.APP_REQUEST;
        EzyArray data = EzyEntityArrays.newArray("test");
        EzyRequest request = mock(EzyRequest.class);
        when(request.getCommand()).thenReturn(cmd);
        when(request.serialize()).thenReturn(data);
        EzyArray message = EzyEntityFactory.newArrayBuilder()
            .append(cmd.getId())
            .append(data)
            .build();
        boolean encrypted = RandomUtil.randomBoolean();

        // when
        sut.udpSend(request, encrypted);

        // then
        verify(mockSocketClient).udpSendMessage(message, encrypted);
    }
}
