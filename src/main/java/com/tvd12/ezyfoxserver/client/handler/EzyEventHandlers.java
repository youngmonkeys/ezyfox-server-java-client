package com.tvd12.ezyfoxserver.client.handler;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;

/**
 * Created by tavandung12 on 9/30/18.
 */

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
        EzyEventHandler handler = handlers.get(eventType);
        return handler;
    }
    
    @SuppressWarnings("unchecked")
	public void handle(EzyEvent event) {
        EzyEventType eventType = event.getType();
        EzyEventHandler handler = handlers.get(eventType);
        if(handler != null)
            handler.handle(event);
        else
            logger.warn("has no handler for event type: " + eventType);
    }

}
