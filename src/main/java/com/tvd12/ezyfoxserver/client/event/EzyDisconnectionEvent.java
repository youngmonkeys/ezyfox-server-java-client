package com.tvd12.ezyfoxserver.client.event;

public class EzyDisconnectionEvent implements EzyEvent {

    private final int reason;

    public EzyDisconnectionEvent(int reason) {
        this.reason = reason;
    }

    public int getReason() {
        return reason;
    }

    @Override
    public EzyEventType getType() {
        return EzyEventType.DISCONNECTION;
    }
}
