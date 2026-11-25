package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.constant.EzySocketConstants;
import com.tvd12.ezyfoxserver.client.socket.EzyPacket;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketWriter;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EzyUdpSocketWriterTest {

    @Test
    public void writePacketToSocketSuccess() throws Exception {
        // given
        byte[] bytes = new byte[]{1, 2, 3};
        EzyPacket packet = mock(EzyPacket.class);
        when(packet.getData()).thenReturn(bytes);
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.write(any(ByteBuffer.class))).thenReturn(bytes.length);
        TestEzyUdpSocketWriter sut = new TestEzyUdpSocketWriter();
        sut.setDatagramChannel(datagramChannel);

        // when
        int actual = sut.writePacket(packet);

        // then
        Asserts.assertEquals(actual, bytes.length);
        verify(datagramChannel, times(1)).write(any(ByteBuffer.class));
        verify(packet, times(1)).release();
    }

    @Test
    public void writeBigPacketToSocketSuccess() throws Exception {
        // given
        byte[] bytes = new byte[EzySocketConstants.MIN_WRITER_BUFFER_SIZE + 1];
        EzyPacket packet = mock(EzyPacket.class);
        when(packet.getData()).thenReturn(bytes);
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.write(any(ByteBuffer.class))).thenAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgumentAt(0, ByteBuffer.class);
            return buffer.remaining();
        });
        TestEzyUdpSocketWriter sut = new TestEzyUdpSocketWriter();
        sut.setDatagramChannel(datagramChannel);

        // when
        int actual = sut.writePacket(packet);

        // then
        Asserts.assertEquals(actual, bytes.length);
        verify(packet, times(1)).release();
    }

    @Test
    public void writePacketToSocketFailed() throws Exception {
        // given
        EzyPacket packet = mock(EzyPacket.class);
        when(packet.getData()).thenReturn(new byte[]{1, 2, 3});
        DatagramChannel datagramChannel = mock(DatagramChannel.class);
        when(datagramChannel.write(any(ByteBuffer.class))).thenThrow(new IOException("test"));
        TestEzyUdpSocketWriter sut = new TestEzyUdpSocketWriter();
        sut.setDatagramChannel(datagramChannel);

        // when
        int actual = sut.writePacket(packet);

        // then
        Asserts.assertEquals(actual, -1);
        verify(packet, times(1)).release();
    }

    @Test
    public void getThreadNameTest() {
        // given
        TestEzyUdpSocketWriter sut = new TestEzyUdpSocketWriter();

        // when
        // then
        Asserts.assertEquals(sut.threadName(), "udp-socket-writer");
    }

    private static class TestEzyUdpSocketWriter extends EzyUdpSocketWriter {

        private int writePacket(EzyPacket packet) {
            return writePacketToSocket(packet);
        }

        private String threadName() {
            return getThreadName();
        }
    }
}
