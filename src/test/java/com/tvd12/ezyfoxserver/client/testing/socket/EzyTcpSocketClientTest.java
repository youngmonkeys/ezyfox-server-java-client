package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionFailedReason;
import com.tvd12.ezyfoxserver.client.socket.EzySocketReader;
import com.tvd12.ezyfoxserver.client.socket.EzySocketWriter;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketReader;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketWriter;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.*;

public class EzyTcpSocketClientTest {

    @Test
    public void connectNowSuccessAndStartAdapters() throws Exception {
        // given
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("127.0.0.1", 0));
        int port = ((InetSocketAddress) server.getLocalAddress()).getPort();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        TestEzyTcpSocketClient sut = new TestEzyTcpSocketClient();
        sut.setEventLoopGroup(eventLoopGroup);
        FieldUtil.setFieldValue(sut, "host", "127.0.0.1");
        FieldUtil.setFieldValue(sut, "port", port);

        try {
            // when
            boolean connected = sut.connect();
            sut.createAndStartAdapters();

            // then
            Asserts.assertTrue(connected);
            SocketChannel socket = FieldUtil.getFieldValue(sut, "socket");
            Asserts.assertTrue(socket.isConnected());
            Asserts.assertFalse(socket.isBlocking());
            EzySocketReader socketReader = FieldUtil.getFieldValue(sut, "socketReader");
            EzySocketWriter socketWriter = FieldUtil.getFieldValue(sut, "socketWriter");
            Asserts.assertEqualsType(socketReader, EzyTcpSocketReader.class);
            Asserts.assertEqualsType(socketWriter, EzyTcpSocketWriter.class);
            Asserts.assertTrue(socketReader.isActive());
            Asserts.assertTrue(socketWriter.isActive());
            Asserts.assertEquals(FieldUtil.getFieldValue(socketReader, "socket"), socket);
            Asserts.assertEquals(FieldUtil.getFieldValue(socketWriter, "socket"), socket);
            verify(eventLoopGroup, times(1)).addEvent(socketReader);
            verify(eventLoopGroup, times(1)).addEvent(socketWriter);

            sut.close0();
            Asserts.assertFalse(socket.isOpen());
            sut.reset();
            Asserts.assertNull(FieldUtil.getFieldValue(sut, "socket"));
        } finally {
            sut.stopAdapters();
            server.close();
        }
    }

    @Test
    public void connectNowFailed() throws Exception {
        // given
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("127.0.0.1", 0));
        int port = ((InetSocketAddress) server.getLocalAddress()).getPort();
        server.close();
        TestEzyTcpSocketClient sut = new TestEzyTcpSocketClient();
        FieldUtil.setFieldValue(sut, "host", "127.0.0.1");
        FieldUtil.setFieldValue(sut, "port", port);

        // when
        boolean connected = sut.connect();

        // then
        Asserts.assertFalse(connected);
        Asserts.assertEquals(
            FieldUtil.getFieldValue(sut, "connectionFailedReason"),
            EzyConnectionFailedReason.CONNECTION_REFUSED
        );
    }

    @Test
    public void connectNowWithoutEventLoopGroupUseBlockingSocket() throws Exception {
        // given
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("127.0.0.1", 0));
        int port = ((InetSocketAddress) server.getLocalAddress()).getPort();
        TestEzyTcpSocketClient sut = new TestEzyTcpSocketClient();
        FieldUtil.setFieldValue(sut, "host", "127.0.0.1");
        FieldUtil.setFieldValue(sut, "port", port);

        try {
            // when
            boolean connected = sut.connect();

            // then
            Asserts.assertTrue(connected);
            SocketChannel socket = FieldUtil.getFieldValue(sut, "socket");
            Asserts.assertTrue(socket.isConnected());
            Asserts.assertTrue(socket.isBlocking());
            sut.close0();
            Asserts.assertFalse(socket.isOpen());
        } finally {
            server.close();
        }
    }

    @Test
    public void closeSocketError() {
        // given
        SocketChannel socket = mock(SocketChannel.class);
        TestEzyTcpSocketClient sut = new TestEzyTcpSocketClient();
        FieldUtil.setFieldValue(sut, "socket", socket);

        // when
        // then
        sut.close0();
    }

    @Test
    public void closeSocketWhenSocketNull() {
        // given
        TestEzyTcpSocketClient sut = new TestEzyTcpSocketClient();

        // when
        // then
        sut.close0();
    }

    private static class TestEzyTcpSocketClient extends EzyTcpSocketClient {

        private TestEzyTcpSocketClient() {
            super(EzyClientConfig.builder().build());
        }

        private boolean connect() {
            return connectNow();
        }

        private void createAndStartAdapters() {
            createAdapters();
            updateAdapters();
            startAdapters();
        }

        private void stopAdapters() {
            clearAdapters();
        }

        private void close0() {
            closeSocket();
        }

        private void reset() {
            resetSocket();
        }
    }
}
