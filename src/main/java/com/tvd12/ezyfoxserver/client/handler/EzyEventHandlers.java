package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class EzyEventHandlers extends EzyAbstractHandlers {

    private final Map<EzyConstant, EzyEventHandler> handlers;

    public EzyEventHandlers(EzyClient client, EzyPingSchedule pingSchedule) {
        super(client, pingSchedule);
        this.handlers = new HashMap<>();
    }

    public void addHandler(EzyConstant eventType, EzyEventHandler handler) {
        this.configHandler(handler);
        this.handlers.put(eventType, handler);
    }

    public EzyEventHandler getHandler(EzyConstant eventType) {
        return handlers.get(eventType);
    }

    @SuppressWarnings("unchecked")
    public void handle(EzyEvent event) {
        EzyEventType eventType = event.getType();
        EzyEventHandler handler = handlers.get(eventType);
        if (handler != null) {
            handler.handle(event);
        } else {
            logger.info("has no handler for event type: " + eventType);
        }
    }
}
