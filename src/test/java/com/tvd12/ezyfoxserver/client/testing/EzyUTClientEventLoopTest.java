package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzySslType;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Random;

import static org.mockito.Mockito.*;

@SuppressWarnings("resource")
public class EzyUTClientEventLoopTest extends BaseTest {

    @Test
    public void creation() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);

        // when
        EzyUTClient sut = new EzyUTClient(config, eventLoopGroup);

        // then
        assert FieldUtil.getFieldValue(sut.getSocket(), "eventLoopGroup") == eventLoopGroup;
        assert FieldUtil.getFieldValue(sut.getPingSchedule(), "eventLoopGroup") == eventLoopGroup;
    }

    @Test
    public void creationWithSsl() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .enableSSL()
            .sslType(EzySslType.CERTIFICATION)
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);

        // when
        // then
        new EzyUTClient(config, eventLoopGroup);
    }

    @Test
    public void tcpConnect() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyUTClient sut = new EzyUTClient(config, eventLoopGroup);

        // when
        sut.connect("unknown", 0);

        // then
        verify(eventLoopGroup, times(1))
            .addOneTimeEvent(any(Runnable.class), anyLong());
    }

    @Test
    public void udpConnectHostPort() throws Exception {
        // given
        String host = "localhost";
        int port = 10000 + new Random().nextInt(10000);
        DatagramChannel fakeServer = startUdpServer(port);
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyUTClient sut = new EzyUTClient(config, eventLoopGroup);
        sut.setSessionToken("testSessionToken");

        try {
            // when
            sut.udpConnect(host, port);

            // then
            Object udpClient = FieldUtil.getFieldValue(sut.getSocket(), "udpClient");
            assert FieldUtil.getFieldValue(udpClient, "eventLoopGroup") == eventLoopGroup;
            verify(eventLoopGroup, times(1))
                .addOneTimeEvent(any(Runnable.class), anyLong());
        } finally {
            fakeServer.close();
        }
    }

    @Test
    public void udpConnectPort() throws Exception {
        // given
        String host = "localhost";
        int port = 10000 + new Random().nextInt(10000);
        DatagramChannel fakeServer = startUdpServer(port);
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyUTClient sut = new EzyUTClient(config, eventLoopGroup);
        sut.setSessionToken("testSessionToken");
        sut.connect(host, 0);

        try {
            // when
            sut.udpConnect(port);

            // then
            verify(eventLoopGroup, times(2))
                .addOneTimeEvent(any(Runnable.class), anyLong());
        } finally {
            fakeServer.close();
        }
    }

    @Test
    public void pingScheduleStart() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .build();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyUTClient sut = new EzyUTClient(config, eventLoopGroup);

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
        EzyUTClient sut = new EzyUTClient(config, eventLoopGroup);
        sut.getPingSchedule().start();

        // when
        sut.getPingSchedule().stop();

        // then
        verify(eventLoopGroup, times(1)).removeEvent(sut.getPingSchedule());
    }

    private DatagramChannel startUdpServer(int port) throws Exception {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.socket().bind(new InetSocketAddress("0.0.0.0", port));
        datagramChannel.socket().setReuseAddress(true);
        Selector udpSelector = Selector.open();
        datagramChannel.register(udpSelector, SelectionKey.OP_READ);
        return datagramChannel;
    }
}
