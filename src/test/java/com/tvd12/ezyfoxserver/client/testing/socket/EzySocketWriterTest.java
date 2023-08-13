package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.socket.EzyPacket;
import com.tvd12.ezyfoxserver.client.socket.EzyPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzySocketWriter;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

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
}
