package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.socket.EzyBlockingPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzyPacket;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class EzyBlockingPacketQueueTest extends BaseTest {

    @Test
    public void addMaxCapacityProcessingTrue() {
        // given
        int capacity = 2;
        EzyBlockingPacketQueue sut = new EzyBlockingPacketQueue(capacity);

        // when
        // then
        EzyPacket packet = mock(EzyPacket.class);
        assert sut.isEmpty();

        sut.add(packet);
        assert !sut.isFull();

        FieldUtil.setFieldValue(sut, "processing", true);
        sut.add(packet);
        sut.again();
        boolean success = sut.add(packet);

        assert !success;
        assert !sut.isEmpty();
        assert sut.size() == 2;
        assert sut.isFull();
    }

    @Test
    public void take() {
        // given
        EzyBlockingPacketQueue sut = new EzyBlockingPacketQueue();

        EzyPacket packet = mock(EzyPacket.class);
        sut.add(packet);

        // when
        EzyPacket actual = sut.take();
        assert actual == packet;
    }

    @Test
    public void peekNowWhenEmpty() {
        // given
        EzyBlockingPacketQueue sut = new EzyBlockingPacketQueue();

        // when
        EzyPacket actual = sut.peekNow();

        // then
        assert actual == null;
    }

    @Test
    public void peekNowWhenProcessing() {
        // given
        EzyBlockingPacketQueue sut = new EzyBlockingPacketQueue();
        EzyPacket packet = mock(EzyPacket.class);
        sut.add(packet);
        assert sut.peekNow() == packet;

        // when
        EzyPacket actual = sut.peekNow();

        // then
        assert actual == null;
        sut.again();
        assert sut.peekNow() == packet;
        assert sut.take() == packet;
        assert sut.peekNow() == null;
    }

    @Test
    public void wakeupThenClear() throws Exception {
        // given
        EzyBlockingPacketQueue sut = new EzyBlockingPacketQueue();

        // when
        sut.wakeup();

        // then
        assert sut.peek() == null;
        assert sut.size() == 1;
        sut.clear();
        assert sut.isEmpty();
        assert sut.peekNow() == null;
    }
}
