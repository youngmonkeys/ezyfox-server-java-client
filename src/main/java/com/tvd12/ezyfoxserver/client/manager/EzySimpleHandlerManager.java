package com.tvd12.ezyfoxserver.client.manager;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyAppAccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyAppExitHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppResponseHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionFailureHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyDisconnectionHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginErrorHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginSuccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyPongHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyUdpHandshakeHandler;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;

import lombok.Getter;

/**
 * Created by tavandung12 on 10/9/18.
 */

@SuppressWarnings("rawtypes")
public class EzySimpleHandlerManager implements EzyHandlerManager {

    private final EzyClient client;
    private final EzyPingSchedule pingSchedule;
    @Getter
    private final EzyEventHandlers eventHandlers;
    @Getter
    private final EzyDataHandlers dataHandlers;
    private final Map<String, EzyAppDataHandlers> appDataHandlerss;
    private final Map<String, EzyPluginDataHandlers> pluginDataHandlerss;

    public EzySimpleHandlerManager(EzyClient client) {
        this.client = client;
        this.pingSchedule = client.getPingSchedule();
        this.eventHandlers = newEventHandlers();
        this.dataHandlers = newDataHandlers();
        this.appDataHandlerss = new HashMap<>();
        this.pluginDataHandlerss = new HashMap<>();
    }

    private EzyEventHandlers newEventHandlers() {
        EzyEventHandlers handlers = new EzyEventHandlers(client, pingSchedule);
        handlers.addHandler(EzyEventType.CONNECTION_SUCCESS, new EzyConnectionSuccessHandler());
        handlers.addHandler(EzyEventType.CONNECTION_FAILURE, new EzyConnectionFailureHandler());
        handlers.addHandler(EzyEventType.DISCONNECTION, new EzyDisconnectionHandler());
        return handlers;
    }

    private EzyDataHandlers newDataHandlers() {
        EzyDataHandlers handlers = new EzyDataHandlers(client, pingSchedule);
        handlers.addHandler(EzyCommand.PONG, new EzyPongHandler());
        handlers.addHandler(EzyCommand.LOGIN, new EzyLoginSuccessHandler());
        handlers.addHandler(EzyCommand.LOGIN_ERROR, new EzyLoginErrorHandler());
        handlers.addHandler(EzyCommand.APP_ACCESS, new EzyAppAccessHandler());
        handlers.addHandler(EzyCommand.APP_REQUEST, new EzyAppResponseHandler());
        handlers.addHandler(EzyCommand.APP_EXIT, new EzyAppExitHandler());
        handlers.addHandler(EzyCommand.UDP_HANDSHAKE, new EzyUdpHandshakeHandler());
        return handlers;
    }

    @Override
    public EzyDataHandler getDataHandler(Object cmd) {
        return dataHandlers.getHandler(cmd);
    }

    @Override
    public EzyEventHandler getEventHandler(EzyConstant eventType) {
        return eventHandlers.getHandler(eventType);
    }

    @Override
    public EzyAppDataHandlers getAppDataHandlers(String appName) {
        EzyAppDataHandlers answer = appDataHandlerss.get(appName);
        if(answer == null) {
            answer = new EzyAppDataHandlers();
            appDataHandlerss.put(appName, answer);
        }
        return answer;
    }
    
    @Override
    public EzyPluginDataHandlers getPluginDataHandlers(String pluginName) {
    	EzyPluginDataHandlers answer = pluginDataHandlerss.get(pluginName);
        if(answer == null) {
            answer = new EzyPluginDataHandlers();
            pluginDataHandlerss.put(pluginName, answer);
        }
        return answer;
    }

    @Override
    public void addDataHandler(Object cmd, EzyDataHandler dataHandler) {
        dataHandlers.addHandler(cmd, dataHandler);
    }

    @Override
    public void addEventHandler(EzyConstant eventType, EzyEventHandler eventHandler) {
        eventHandlers.addHandler(eventType, eventHandler);
    }
}
