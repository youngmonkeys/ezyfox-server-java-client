package com.tvd12.ezyfoxserver.client.testing.event;

import com.tvd12.ezyfoxserver.client.constant.EzyConnectionFailedReason;
import com.tvd12.ezyfoxserver.client.event.EzyConnectionFailureEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import org.testng.annotations.Test;

public class EzyConnectionFailureEventTest {

    @Test
    public void networkUnreachable() {
        // given
        EzyConnectionFailureEvent event = EzyConnectionFailureEvent.networkUnreachable();

        // when
        // then
        assert event.getType() == EzyEventType.CONNECTION_FAILURE;
        assert event.getReason() == EzyConnectionFailedReason.NETWORK_UNREACHABLE;
    }

    @Test
    public void unknownHost() {
        // given
        EzyConnectionFailureEvent event = EzyConnectionFailureEvent.unknownHost();

        // when
        // then
        assert event.getReason() == EzyConnectionFailedReason.UNKNOWN_HOST;
    }

    @Test
    public void connectionRefused() {
        // given
        EzyConnectionFailureEvent event = EzyConnectionFailureEvent.connectionRefused();

        // when
        // then
        assert event.getReason() == EzyConnectionFailedReason.CONNECTION_REFUSED;
    }

    @Test
    public void unknown() {
        // given
        EzyConnectionFailureEvent event = EzyConnectionFailureEvent.unknown();

        // when
        // then
        assert event.getReason() == EzyConnectionFailedReason.UNKNOWN;
    }
}
