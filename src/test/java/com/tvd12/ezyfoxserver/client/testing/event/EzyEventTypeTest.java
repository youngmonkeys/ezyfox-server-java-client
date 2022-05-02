package com.tvd12.ezyfoxserver.client.testing.event;

import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import org.testng.annotations.Test;

public class EzyEventTypeTest {

    @Test
    public void test() {
        // given
        EzyEventType eventType = EzyEventType.CONNECTION_SUCCESS;

        // when
        // then
        assert eventType.getId() == 1;
        assert eventType.getName().equals("CONNECTION_SUCCESS");
    }
}
