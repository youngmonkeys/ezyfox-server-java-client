package com.tvd12.ezyfoxserver.client.testing.event;

import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.event.EzyLostPingEvent;
import org.testng.annotations.Test;

import java.util.Random;

public class EzyLostPingEventTest {

    @Test
    public void test() {
        // given
        int count = new Random().nextInt();
        EzyLostPingEvent event = new EzyLostPingEvent(count);

        // when
        // then
        assert event.getCount() == count;
        assert event.getType() == EzyEventType.LOST_PING;
    }
}

