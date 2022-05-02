package com.tvd12.ezyfoxserver.client.event;

public class EzyConnectionSuccessEvent implements EzyEvent {

    @Override
    public EzyEventType getType() {
        return EzyEventType.CONNECTION_SUCCESS;
    }
}
