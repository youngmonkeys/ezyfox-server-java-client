package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.constant.EzySocketConstants;
import com.tvd12.ezyfoxserver.client.socket.EzyBlockingPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzyPacket;
import com.tvd12.ezyfoxserver.client.socket.EzyPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzySimplePacket;
import com.tvd12.ezyfoxserver.client.socket.EzySocketWriter;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.mockito.Mockito.*;

public class EzySocketWriterTest extends BaseTest {

    @Test
    public void loopWithPacketNull() throws Exception {
        // given
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peek()).thenReturn(null);
        EzySocketWriter sut = spy(EzySocketWriter.class);
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();
    }

    @Test
    public void writePackageOK() throws Exception {
        // given
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        EzyPacket packet = mock(EzyPacket.class);
        when(packet.getData()).thenReturn(new byte[]{1, 2, 3});
        when(packet.isReleased()).thenReturn(true);
        when(packetQueue.peek()).thenReturn(packet);

        EzySocketWriter sut = spy(EzySocketWriter.class);
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();
    }

    @Test
    public void writePackageAgain() throws Exception {
        // given
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        EzyPacket packet = mock(EzyPacket.class);
        when(packet.getData()).thenReturn(new byte[]{1, 2, 3});
        when(packetQueue.peek()).thenReturn(packet);

        EzySocketWriter sut = spy(EzySocketWriter.class);
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();
    }

    @Test
    public void loopWriteFullyThenNoPacket() throws Exception {
        // given
        EzySimplePacket packet = newPacket(new byte[]{1, 2, 3});
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peek()).thenReturn(packet, (EzyPacket) null);
        TestEzySocketWriter sut = new TestEzySocketWriter(ByteBuffer::remaining);
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();

        // then
        waitStopped(sut);
        Asserts.assertTrue(packet.isReleased());
        verify(packetQueue, times(1)).take();
        verify(packetQueue, times(0)).again();
    }

    @Test
    public void loopWriteFailed() throws Exception {
        // given
        EzySimplePacket packet = newPacket(new byte[]{1, 2, 3});
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peek()).thenReturn(packet);
        TestEzySocketWriter sut = new TestEzySocketWriter(buffer -> -1);
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();

        // then
        waitStopped(sut);
        verify(packetQueue, times(1)).peek();
        verify(packetQueue, times(0)).take();
        verify(packetQueue, times(0)).again();
    }

    @Test
    public void loopInterrupted() throws Exception {
        // given
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peek()).thenThrow(new InterruptedException("test"));
        TestEzySocketWriter sut = new TestEzySocketWriter(ByteBuffer::remaining);
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();

        // then
        waitStopped(sut);
        verify(packetQueue, times(1)).peek();
    }

    @Test
    public void loopErrorThenStop() throws Exception {
        // given
        AtomicInteger peekCount = new AtomicInteger();
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        TestEzySocketWriter sut = new TestEzySocketWriter(ByteBuffer::remaining);
        when(packetQueue.peek()).thenAnswer(invocation -> {
            if (peekCount.incrementAndGet() > 1) {
                sut.stop();
            }
            throw new IllegalStateException("test");
        });
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();

        // then
        waitStopped(sut);
        Asserts.assertEquals(peekCount.get(), 2);
    }

    @Test
    public void loopWritePacketErrorThenWriteNextPacket() throws Exception {
        // given
        EzyBlockingPacketQueue packetQueue = new EzyBlockingPacketQueue();
        EzySimplePacket errorPacket = new EzySimplePacket();
        EzySimplePacket nextPacket = newPacket(new byte[]{1, 2, 3});
        packetQueue.add(errorPacket);
        packetQueue.add(nextPacket);
        AtomicInteger writtenBytes = new AtomicInteger();
        AtomicReference<TestEzySocketWriter> sutRef = new AtomicReference<>();
        TestEzySocketWriter sut = new TestEzySocketWriter(buffer -> {
            int bytes = buffer.remaining();
            writtenBytes.addAndGet(bytes);
            sutRef.get().stop();
            return bytes;
        });
        sutRef.set(sut);
        sut.setPacketQueue(packetQueue);

        // when
        sut.start();

        // then
        waitStopped(sut);
        Asserts.assertEquals(writtenBytes.get(), 3);
        Asserts.assertTrue(errorPacket.isReleased());
        Asserts.assertTrue(nextPacket.isReleased());
    }

    @Test
    public void callWhenInactive() {
        // given
        TestEzySocketWriter sut = new TestEzySocketWriter(ByteBuffer::remaining);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
    }

    @Test
    public void callWhenNoPacket() {
        // given
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peekNow()).thenReturn(null);
        TestEzySocketWriter sut = new TestEzySocketWriter(ByteBuffer::remaining);
        sut.setPacketQueue(packetQueue);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        sut.stop();
    }

    @Test
    public void callWriteFully() {
        // given
        EzySimplePacket packet = newPacket(new byte[]{1, 2, 3});
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peekNow()).thenReturn(packet);
        TestEzySocketWriter sut = new TestEzySocketWriter(ByteBuffer::remaining);
        sut.setPacketQueue(packetQueue);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        Asserts.assertTrue(packet.isReleased());
        verify(packetQueue, times(1)).take();
        verify(packetQueue, times(0)).again();
        sut.stop();
    }

    @Test
    public void callWritePartially() {
        // given
        EzySimplePacket packet = newPacket(new byte[]{1, 2, 3});
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peekNow()).thenReturn(packet);
        TestEzySocketWriter sut = new TestEzySocketWriter(buffer -> {
            buffer.get();
            return 1;
        });
        sut.setPacketQueue(packetQueue);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        Asserts.assertFalse(packet.isReleased());
        Asserts.assertTrue(packet.isFragmented());
        Asserts.assertEquals(
            Arrays.toString((byte[]) packet.getData()),
            Arrays.toString(new byte[]{2, 3})
        );
        verify(packetQueue, times(0)).take();
        verify(packetQueue, times(1)).again();
        sut.stop();
    }

    @Test
    public void callWriteFailed() {
        // given
        EzySimplePacket packet = newPacket(new byte[]{1, 2, 3});
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peekNow()).thenReturn(packet);
        TestEzySocketWriter sut = new TestEzySocketWriter(buffer -> -1);
        sut.setPacketQueue(packetQueue);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        verify(packetQueue, times(0)).take();
        verify(packetQueue, times(0)).again();
        sut.stop();
    }

    @Test
    public void callWithError() {
        // given
        EzySimplePacket packet = newPacket(null);
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peekNow()).thenReturn(packet);
        TestEzySocketWriter sut = new TestEzySocketWriter(ByteBuffer::remaining);
        sut.setPacketQueue(packetQueue);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        sut.stop();
    }

    @Test
    public void callWriteBigPacket() {
        // given
        byte[] bytes = new byte[EzySocketConstants.MIN_WRITER_BUFFER_SIZE + 1];
        EzySimplePacket packet = newPacket(bytes);
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);
        when(packetQueue.peekNow()).thenReturn(packet);
        AtomicInteger writtenBytes = new AtomicInteger();
        TestEzySocketWriter sut = new TestEzySocketWriter(buffer -> {
            writtenBytes.set(buffer.remaining());
            buffer.position(buffer.limit());
            return writtenBytes.get();
        });
        sut.setPacketQueue(packetQueue);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        Asserts.assertEquals(writtenBytes.get(), bytes.length);
        Asserts.assertTrue(packet.isReleased());
        sut.stop();
    }

    private static EzySimplePacket newPacket(byte[] bytes) {
        EzySimplePacket packet = new EzySimplePacket();
        packet.setData(bytes);
        return packet;
    }

    private static void startWithEventLoop(EzySocketWriter sut) {
        sut.setEventLoopGroup(mock(EzyEventLoopGroup.class));
        sut.start();
    }

    private static void waitStopped(EzySocketWriter sut) throws Exception {
        long startTime = System.currentTimeMillis();
        while (!sut.isStopped()) {
            if (System.currentTimeMillis() - startTime > 3000) {
                throw new IllegalStateException("socket writer is not stopped");
            }
            Thread.sleep(10);
        }
    }

    private static class TestEzySocketWriter extends EzySocketWriter {

        private final Function<ByteBuffer, Integer> writer;

        private TestEzySocketWriter(Function<ByteBuffer, Integer> writer) {
            this.writer = writer;
        }

        @Override
        protected int writeToSocket(ByteBuffer buffer) {
            return writer.apply(buffer);
        }
    }
}
