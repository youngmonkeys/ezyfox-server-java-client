package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.codec.EzySimpleCodecFactory;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketReader;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("resource")
public class EzyUdpSocketClientTest extends BaseTest {

    @Test
    public void connectTo() throws Exception {
        // given
        String host = "localhost";
        int port = 10000 + new Random().nextInt(10000);
        DatagramChannel datagramChannel = startUdpServer(port);
        EzySimpleCodecFactory codecFactory = new EzySimpleCodecFactory(false);
        EzyUdpSocketClient sut = new EzyUdpSocketClient(codecFactory);
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        sut.setSessionId(sessionId);
        sut.setSessionToken(sessionToken);

        // when
        sut.connectTo(host, port);

        // then
        Thread.sleep(350);
        sut.close();
        datagramChannel.close();
    }

    @Test
    public void connectToAndTryReconnect() throws Exception {
        // given
        String host = "localhost";
        int port = 10000 + new Random().nextInt(10000);
        DatagramChannel datagramChannel = startUdpServer(port);
        EzySimpleCodecFactory codecFactory = new EzySimpleCodecFactory(false);
        EzyUdpSocketClient sut = new EzyUdpSocketClient(codecFactory) {
            @Override
            protected int sleepTimeBeforeReconnect() {
                return 100;
            }
        };
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        sut.setSessionId(sessionId);
        sut.setSessionToken(sessionToken);

        // when
        // then
        sut.connectTo(host, port);
        Thread.sleep(350);
        sut.close();
        datagramChannel.close();
    }

    @Test
    public void connectToWithEvenLoopGroup() throws Exception {
        // given
        String host = "localhost";
        int port = 10000 + new Random().nextInt(10000);
        DatagramChannel datagramChannel = startUdpServer(port);
        EzySimpleCodecFactory codecFactory = new EzySimpleCodecFactory(false);
        EzyUdpSocketClient sut = new EzyUdpSocketClient(codecFactory);
        String sessionToken = "testSessionToken";
        long sessionId = new Random().nextLong();
        sut.setSessionId(sessionId);
        sut.setSessionToken(sessionToken);

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        sut.setEventLoopGroup(eventLoopGroup);

        // when
        sut.connectTo(host, port);

        // then
        sut.close();
        datagramChannel.close();

        verify(eventLoopGroup, times(1)).addOneTimeEvent(
            any(Runnable.class),
            any(long.class)
        );
    }

    @Test
    public void sendMessageSuccess() {
        // given
        EzySimpleCodecFactory codecFactory = new EzySimpleCodecFactory(false);
        EzyUdpSocketClient sut = new EzyUdpSocketClient(codecFactory);
        boolean encrypted = RandomUtil.randomBoolean();

        // when
        // then
        EzyArray message = EzyEntityArrays.newArray("test");
        sut.sendMessage(message, encrypted);
    }

    @Test
    public void popReadMessagesWhenConnected() {
        // given
        EzyUdpSocketReader mockSocketReader = mock(EzyUdpSocketReader.class);

        EzySimpleCodecFactory codecFactory = new EzySimpleCodecFactory(false);
        EzyUdpSocketClient sut = new EzyUdpSocketClient(codecFactory);

        FieldUtil.setFieldValue(sut, "socketReader", mockSocketReader);

        sut.setStatus(EzySocketStatus.CONNECTED);

        // when
        // then
        List<EzyArray> buffer = new ArrayList<>();
        sut.popReadMessages(buffer);
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
