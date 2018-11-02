package com.tvd12.ezyfoxserver.client.socket;


import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;

import java.util.Set;

/**
 * Created by tavandung12 on 9/30/18.
 */

public class EzySocketDataEventHandler extends EzyAbstractSocketEventHandler {

    private final EzySocketDataHandler dataHandler;
    private final EzyPingManager pingManager;
    private final EzyHandlerManager handlerManager;
    private final EzyMainThreadQueue mainThreadQueue;
    private final EzySocketEventQueue socketEventQueue;
    private final Set<Object> unloggableCommands;

    public EzySocketDataEventHandler(EzyMainThreadQueue mainThreadQueue,
                                     EzySocketDataHandler dataHandler,
                                     EzyPingManager pingManager,
                                     EzyHandlerManager handlerManager,
                                     EzySocketEventQueue socketEventQueue,
                                     Set<Object> unloggableCommands) {
        this.dataHandler = dataHandler;
        this.pingManager = pingManager;
        this.handlerManager = handlerManager;
        this.mainThreadQueue = mainThreadQueue;
        this.socketEventQueue = socketEventQueue;
        this.unloggableCommands = unloggableCommands;
    }

    @Override
    public void handleEvent() {
        try {
            EzySocketEvent event = socketEventQueue.take();
            EzyConstant eventType = event.getType();
            Object eventData = event.getData();
            if(eventType == EzySocketEventType.EVENT)
                processEvent((EzyEvent)eventData);
            else
                processResponse((EzyResponse)eventData);
        } catch (InterruptedException e) {
            getLogger().error("can't take socket response", e);
        }
    }

    @SuppressWarnings("rawtypes")
	private void processEvent(final EzyEvent event) {
        EzyEventType eventType = event.getType();
        final EzyEventHandler handler = handlerManager.getEventHandler(eventType);
        if(handler != null)
            mainThreadQueue.add(event, handler);
        else
            getLogger().warn("has no handler with event: " + eventType);
    }

    private void processResponse(EzyResponse response) {
        pingManager.setLostPingCount(0);
        EzyConstant cmd = response.getCommand();
        EzyArray data = response.getData();
        EzyArray responseData = data.getWithDefault(1, null);
        if(!unloggableCommands.contains(cmd))
            getLogger().debug("received command: " + cmd + " and data: " + responseData);
        if(cmd == EzyCommand.DISCONNECT)
            handleDisconnection(responseData);
        else
            handleResponseData(cmd, responseData);
    }

    private void handleDisconnection(EzyArray responseData) {
        int reasonId = responseData.get(0, int.class, 0);
        dataHandler.fireSocketDisconnected(reasonId);
    }

    private void handleResponseData(final Object cmd, final EzyArray responseData) {
        final EzyDataHandler handler = handlerManager.getDataHandler(cmd);
        if (handler != null)
        		mainThreadQueue.add(responseData, handler);
        else 
            getLogger().debug("has no handler with command: " + cmd);
    }
}
