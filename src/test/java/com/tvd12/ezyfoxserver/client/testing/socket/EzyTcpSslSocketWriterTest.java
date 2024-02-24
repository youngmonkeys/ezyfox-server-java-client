package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.socket.EzyPacket;
import com.tvd12.ezyfoxserver.client.socket.EzyPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSslSocketWriter;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.io.OutputStream;

import static org.mockito.Mockito.*;

public class EzyTcpSslSocketWriterTest {

    @Test
    public void writePacketToSocketTest() throws Exception {
        // given
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);

        EzyPacket packet = mock(EzyPacket.class);
        byte[] data = RandomUtil.randomShortByteArray();
        when(packet.getData()).thenReturn(data);
        when(packetQueue.peek()).thenReturn(packet);

        EzyTcpSslSocketWriter instance = new EzyTcpSslSocketWriter();
        instance.setPacketQueue(packetQueue);

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        instance.setEventLoopGroup(eventLoopGroup);

        OutputStream outputStream = mock(OutputStream.class);
        instance.setOutputStream(outputStream);

        // then
        instance.start();
        instance.call();

        // then
        verify(outputStream, times(1)).write(data);
        instance.stop();
    }

    @Test
    public void writePacketToSocketThrowExceptionTest() throws Exception {
        // given
        EzyPacketQueue packetQueue = mock(EzyPacketQueue.class);

        EzyPacket packet = mock(EzyPacket.class);
        byte[] data = RandomUtil.randomShortByteArray();
        when(packet.getData()).thenReturn(data);
        when(packetQueue.peek()).thenReturn(packet);

        EzyTcpSslSocketWriter instance = new EzyTcpSslSocketWriter();
        instance.setPacketQueue(packetQueue);

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        instance.setEventLoopGroup(eventLoopGroup);

        OutputStream outputStream = mock(OutputStream.class);
        doThrow(new RuntimeException("test")).when(outputStream)
                .write(any(byte[].class));
        instance.setOutputStream(outputStream);

        // then
        instance.start();
        instance.call();

        // then
        verify(outputStream, times(1)).write(data);
        instance.stop();
    }
}
