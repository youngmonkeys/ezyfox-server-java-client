package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.EzyTcpClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

@SuppressWarnings("resource")
public class EzyTcpClientEventLoopTest extends BaseTest {

    @Test
    public void creation() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);

        // when
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);

        // then
        assert FieldUtil.getFieldValue(sut.getSocket(), "eventLoopGroup") == eventLoopGroup;
        assert FieldUtil.getFieldValue(sut.getPingSchedule(), "eventLoopGroup") == eventLoopGroup;
    }

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
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);

        // when
        sut.connect("unknown", 0);

        // then
        verify(eventLoopGroup, times(1))
            .addOneTimeEvent(any(Runnable.class), anyLong());
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
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);
        sut.setStatus(EzyConnectionStatus.CONNECTED);

        // when
        sut.connect("unknown", 0);

        // then
        verify(eventLoopGroup, never())
            .addOneTimeEvent(any(Runnable.class), anyLong());
    }

    @Test
    public void connectError() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzySocketClient mockSocketClient = mock(EzySocketClient.class);
        String host = "host";
        int port = 0;
        doThrow(new RuntimeException("just test"))
            .when(mockSocketClient)
            .connectTo(host, port);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup) {
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
    public void reconnectAble() {
        // given
        int reconnectPeriod = 3;
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .maxReconnectCount(1)
            .reconnectPeriod(reconnectPeriod)
            .done()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);

        // when
        sut.connect("unknown", 0);
        verify(eventLoopGroup, times(1))
            .addOneTimeEvent(taskCaptor.capture(), anyLong());
        // the shared event loop thread would run this task, simulate that here
        taskCaptor.getValue().run();
        sut.setStatus(EzyConnectionStatus.FAILURE);
        sut.reconnect();

        // then
        verify(eventLoopGroup, times(2))
            .addOneTimeEvent(any(Runnable.class), anyLong());
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
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);

        // when
        sut.connect("unknown", 0);
        sut.reconnect();

        // then
        verify(eventLoopGroup, times(1))
            .addOneTimeEvent(any(Runnable.class), anyLong());
    }

    @Test
    public void reconnectSuccess() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzySocketClient mockSocketClient = mock(EzySocketClient.class);
        when(mockSocketClient.reconnect()).thenReturn(true);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup) {
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
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);

        // when
        // then
        sut.disconnect(EzyDisconnectReason.CLOSE.getId());
    }

    @Test
    public void adaptersJoinEventLoopGroupOnConnect() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);

        // when
        sut.connect("unknown", 0);

        // then
        EzySocketClient socketClient = sut.getSocket();
        Object socketReader = FieldUtil.getFieldValue(socketClient, "socketReader");
        Object socketWriter = FieldUtil.getFieldValue(socketClient, "socketWriter");
        assert FieldUtil.getFieldValue(socketReader, "eventLoopGroup") == eventLoopGroup;
        assert FieldUtil.getFieldValue(socketWriter, "eventLoopGroup") == eventLoopGroup;
    }

    @Test
    public void pingScheduleStart() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);

        // when
        sut.getPingSchedule().start();

        // then
        verify(eventLoopGroup, times(1))
            .addScheduleEvent(eq(sut.getPingSchedule()), anyLong(), anyLong());
    }

    @Test
    public void pingScheduleStop() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyTcpClient sut = new EzyTcpClient(config, eventLoopGroup);
        sut.getPingSchedule().start();

        // when
        sut.getPingSchedule().stop();

        // then
        verify(eventLoopGroup, times(1)).removeEvent(sut.getPingSchedule());
    }
}
