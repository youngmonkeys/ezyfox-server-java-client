package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.codec.EzySimpleCodecFactory;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.socket.EzyPackage;
import com.tvd12.ezyfoxserver.client.socket.EzyResponseApi;
import com.tvd12.ezyfoxserver.client.socket.EzySocketResponseApi;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketReader;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    public void popReadMessagesWhenConnecting() {
        // given
        EzyUdpSocketReader mockSocketReader = mock(EzyUdpSocketReader.class);
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false));
        FieldUtil.setFieldValue(sut, "socketReader", mockSocketReader);
        sut.setStatus(EzySocketStatus.CONNECTING);
        List<EzyArray> buffer = new ArrayList<>();

        // when
        sut.popReadMessages(buffer);

        // then
        verify(mockSocketReader, times(1)).popMessages(buffer);
    }

    @Test
    public void popReadMessagesWhenNotConnected() {
        // given
        EzyUdpSocketReader mockSocketReader = mock(EzyUdpSocketReader.class);
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false));
        FieldUtil.setFieldValue(sut, "socketReader", mockSocketReader);
        sut.setStatus(EzySocketStatus.DISCONNECTED);
        List<EzyArray> buffer = new ArrayList<>();

        // when
        sut.popReadMessages(buffer);

        // then
        verify(mockSocketReader, times(0)).popMessages(buffer);
    }

    @Test
    public void connectToWhenConnecting() {
        // given
        AtomicInteger connectCount = new AtomicInteger();
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false)) {
            @Override
            protected void connect0() {
                connectCount.incrementAndGet();
            }
        };
        sut.setStatus(EzySocketStatus.CONNECTING);

        // when
        sut.connectTo("localhost", 2611);

        // then
        Asserts.assertEquals(connectCount.get(), 0);
    }

    @Test
    public void reconnectWhenNotConnectFailed() {
        // given
        AtomicInteger connectCount = new AtomicInteger();
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false)) {
            @Override
            protected void connect0() {
                connectCount.incrementAndGet();
            }
        };
        sut.setStatus(EzySocketStatus.CONNECTED);

        // when
        boolean actual = sut.reconnect();

        // then
        Asserts.assertFalse(actual);
        Asserts.assertEquals(connectCount.get(), 0);
    }

    @Test
    public void reconnectWhenConnectFailed() {
        // given
        AtomicInteger connectCount = new AtomicInteger();
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false)) {
            @Override
            protected void connect0() {
                connectCount.incrementAndGet();
            }
        };
        sut.setStatus(EzySocketStatus.CONNECT_FAILED);

        // when
        boolean actual = sut.reconnect();

        // then
        Asserts.assertTrue(actual);
        Asserts.assertEquals(connectCount.get(), 1);
    }

    @Test
    public void connectRetryWhenBindException() {
        // given
        AtomicInteger connectCount = new AtomicInteger();
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false)) {
            @Override
            protected void connect1() throws BindException {
                if (connectCount.incrementAndGet() < 3) {
                    throw new BindException("test");
                }
            }
        };

        // when
        sut.connectTo("localhost", 2611);

        // then
        Asserts.assertEquals(connectCount.get(), 3);
    }

    @Test
    public void connectToUnresolvedHost() {
        // given
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false));
        sut.setSessionToken("testSessionToken");

        // when
        Throwable e = Asserts.assertThrows(() ->
            sut.connectTo("unknown.host.invalid", 2611)
        );

        // then
        Asserts.assertEqualsType(e, IllegalStateException.class);
        sut.close();
    }

    @Test
    public void sendMessageError() throws Exception {
        // given
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false));
        EzyResponseApi responseApi = mock(EzyResponseApi.class);
        doThrow(new IllegalStateException("test"))
            .when(responseApi).response(any(EzyPackage.class));
        FieldUtil.setFieldValue(sut, "responseApi", responseApi);

        // when
        sut.sendMessage(EzyEntityArrays.newArray("test"), false);

        // then
        verify(responseApi, times(1)).response(any(EzyPackage.class));
    }

    @Test
    public void setCompressionEnableTest() {
        // given
        EzyUdpSocketClient sut = new EzyUdpSocketClient(new EzySimpleCodecFactory(false), 128);

        // when
        sut.setCompressionEnable(true);

        // then
        EzySocketResponseApi responseApi = FieldUtil.getFieldValue(sut, "responseApi");
        Asserts.assertTrue(responseApi.isCompressionEnable());
        Asserts.assertEquals(FieldUtil.getFieldValue(responseApi, "compressionThreshold"), 128);
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
