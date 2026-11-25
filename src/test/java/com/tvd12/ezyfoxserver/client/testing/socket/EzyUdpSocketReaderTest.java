package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.socket.EzySocketDataDecoder;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketReader;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

public class EzyUdpSocketReaderTest extends BaseTest {

    private static final byte[] MESSAGE_BYTES = new byte[]{0, 0, 1, 1};

    @Test
    public void loop() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        EzyArray data = EzyEntityArrays.newArray("test");
        when(decoder.decode(any(EzyMessage.class), any(byte[].class))).thenReturn(data);

        EzyUdpSocketReader sut = new EzyUdpSocketReader() {
            @Override
            protected int readSocketData() {
                buffer.put((byte) 0);
                buffer.put(new byte[]{0, 1, 1});
                return 4;
            }
        };
        sut.setDecoder(decoder);

        // when
        // then
        sut.start();
        Thread.sleep(250);
        sut.stop();
    }

    @Test
    public void loopWithDatagramChannel() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        EzyArray data = EzyEntityArrays.newArray("test");
        when(decoder.decode(any(EzyMessage.class), any(byte[].class))).thenReturn(data);

        AtomicInteger receiveCount = new AtomicInteger();
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.receive(any(ByteBuffer.class))).thenAnswer(invocation -> {
            if (receiveCount.incrementAndGet() > 1) {
                throw new IOException("test");
            }
            ByteBuffer buffer = invocation.getArgumentAt(0, ByteBuffer.class);
            buffer.put(MESSAGE_BYTES);
            return new InetSocketAddress("localhost", 2611);
        });

        EzyUdpSocketReader sut = new EzyUdpSocketReader();
        sut.setDecoder(decoder);
        sut.setDatagramChannel(datagramChannel);

        // when
        sut.start();
        waitStopped(sut);

        // then
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertEquals(buffer.size(), 1);
        Asserts.assertEquals(buffer.get(0), data);
        Asserts.assertEquals(receiveCount.get(), 2);
    }

    @Test
    public void loopInterrupted() throws Exception {
        // given
        EzyUdpSocketReader sut = new EzyUdpSocketReader() {
            @Override
            protected int readSocketData() throws Exception {
                throw new InterruptedException("test");
            }
        };

        // when
        sut.start();

        // then
        waitStopped(sut);
    }

    @Test
    public void loopError() throws Exception {
        // given
        EzyUdpSocketReader sut = new EzyUdpSocketReader() {
            @Override
            protected int readSocketData() {
                throw new IllegalStateException("test");
            }
        };

        // when
        sut.start();

        // then
        waitStopped(sut);
    }

    @Test
    public void callWhenInactive() {
        // given
        EzyUdpSocketReader sut = new EzyUdpSocketReader();

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
    }

    @Test
    public void callWhenNoData() throws Exception {
        // given
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.receive(any(ByteBuffer.class))).thenReturn(null);
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        sut.stop();
    }

    @Test
    public void callWhenEmptyData() throws Exception {
        // given
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.receive(any(ByteBuffer.class)))
            .thenReturn(new InetSocketAddress("localhost", 2611));
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        sut.stop();
    }

    @Test
    public void callWithData() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        EzyArray data = EzyEntityArrays.newArray("test");
        byte[] decryptionKey = new byte[]{1, 2, 3};
        when(decoder.decode(any(EzyMessage.class), any(byte[].class))).thenReturn(data);
        DatagramChannel datagramChannel = newDatagramChannel(MESSAGE_BYTES);
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);
        sut.setDecoder(decoder);
        sut.setDecryptionKey(decryptionKey);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        verify(decoder, times(1)).decode(any(EzyMessage.class), eq(decryptionKey));
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertEquals(buffer.size(), 1);
        Asserts.assertEquals(buffer.get(0), data);
    }

    @Test
    public void callWithInvalidMessage() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        DatagramChannel datagramChannel = newDatagramChannel(new byte[]{0});
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);
        sut.setDecoder(decoder);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        verify(decoder, times(0)).decode(any(EzyMessage.class), any(byte[].class));
        sut.stop();
    }

    @Test
    public void callWithDecodeError() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        when(decoder.decode(any(EzyMessage.class), any(byte[].class)))
            .thenThrow(new IllegalStateException("test"));
        DatagramChannel datagramChannel = newDatagramChannel(MESSAGE_BYTES);
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);
        sut.setDecoder(decoder);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertTrue(buffer.isEmpty());
        sut.stop();
    }

    @Test
    public void callWithReceiveError() throws Exception {
        // given
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.receive(any(ByteBuffer.class)))
            .thenThrow(new IOException("test"));
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        sut.stop();
    }

    @Test
    public void callWithAsynchronousClose() throws Exception {
        // given
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.receive(any(ByteBuffer.class)))
            .thenThrow(new AsynchronousCloseException());
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        sut.stop();
    }

    @Test
    public void callWithUnexpectedError() {
        // given
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyUdpSocketReader sut = new EzyUdpSocketReader() {
            @Override
            protected Integer nonBlockingReadSocketData() {
                throw new IllegalStateException("test");
            }
        };
        sut.setEventLoopGroup(eventLoopGroup);
        sut.start();

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        sut.stop();
    }

    @Test
    public void stopClearDataQueue() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        EzyArray data = EzyEntityArrays.newArray("test");
        when(decoder.decode(any(EzyMessage.class), any(byte[].class))).thenReturn(data);
        DatagramChannel datagramChannel = newDatagramChannel(MESSAGE_BYTES);
        EzyUdpSocketReader sut = newEventLoopReader(datagramChannel);
        sut.setDecoder(decoder);
        sut.call();

        // when
        sut.stop();

        // then
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertTrue(buffer.isEmpty());
    }

    private static DatagramChannel newDatagramChannel(byte[] bytes) throws Exception {
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.receive(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgumentAt(0, ByteBuffer.class);
            buffer.put(bytes);
            return new InetSocketAddress("localhost", 2611);
        });
        return datagramChannel;
    }

    private static EzyUdpSocketReader newEventLoopReader(DatagramChannel datagramChannel) {
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyUdpSocketReader sut = new EzyUdpSocketReader();
        sut.setEventLoopGroup(eventLoopGroup);
        sut.setDatagramChannel(datagramChannel);
        sut.start();
        return sut;
    }

    private static void waitStopped(EzyUdpSocketReader sut) throws Exception {
        long startTime = System.currentTimeMillis();
        while (!sut.isStopped()) {
            if (System.currentTimeMillis() - startTime > 3000) {
                throw new IllegalStateException("socket reader is not stopped");
            }
            Thread.sleep(10);
        }
    }
}
