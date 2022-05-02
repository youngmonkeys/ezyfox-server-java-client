package com.tvd12.ezyfoxserver.client.testing.event;

import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.event.EzyTryConnectEvent;
import org.testng.annotations.Test;

import java.util.Random;

public class EzyTryConnectEventTest {

    @Test
    public void test() {
        // given
        int count = new Random().nextInt();
        EzyTryConnectEvent event = new EzyTryConnectEvent(count);

        // when
        // then
        assert event.getCount() == count;
        assert event.getType() == EzyEventType.TRY_CONNECT;
    }
}
