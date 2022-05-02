package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.*;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class EzySimpleHandlerManager implements EzyHandlerManager {

    private final EzyClient client;
    private final EzyPingSchedule pingSchedule;
    @Getter
    private final EzyEventHandlers eventHandlers;
    @Getter
    private final EzyDataHandlers dataHandlers;
    private final Map<String, EzyAppDataHandlers> appDataHandlersByName;
    private final Map<String, EzyPluginDataHandlers> pluginDataHandlersByName;

    public EzySimpleHandlerManager(EzyClient client) {
        this.client = client;
        this.pingSchedule = client.getPingSchedule();
        this.eventHandlers = newEventHandlers();
        this.dataHandlers = newDataHandlers();
        this.appDataHandlersByName = new HashMap<>();
        this.pluginDataHandlersByName = new HashMap<>();
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
        EzyAppDataHandlers answer = appDataHandlersByName.get(appName);
        if (answer == null) {
            answer = new EzyAppDataHandlers();
            appDataHandlersByName.put(appName, answer);
        }
        return answer;
    }

    @Override
    public EzyPluginDataHandlers getPluginDataHandlers(String pluginName) {
        EzyPluginDataHandlers answer = pluginDataHandlersByName.get(pluginName);
        if (answer == null) {
            answer = new EzyPluginDataHandlers();
            pluginDataHandlersByName.put(pluginName, answer);
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
