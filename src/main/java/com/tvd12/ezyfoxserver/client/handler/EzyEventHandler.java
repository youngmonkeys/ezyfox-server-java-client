package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfoxserver.client.event.EzyEvent;

public interface EzyEventHandler<E extends EzyEvent> {

    void handle(E event);
}
