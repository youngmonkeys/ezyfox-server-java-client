package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.entity.EzyEntity;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfox.util.EzyEquals;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;

public class EzySimpleApp extends EzyEntity implements EzyApp {
    protected final int id;
    protected final String name;
    protected final EzyZone zone;
    protected final EzyClient client;
    protected final EzyAppDataHandlers dataHandlers;

    public EzySimpleApp(EzyZone zone, int id, String name) {
        this.client = zone.getClient();
        this.zone = zone;
        this.id = id;
        this.name = name;
        this.dataHandlers = client.getHandlerManager().getAppDataHandlers(name);
    }

    @Override
    public void send(EzyRequest request) {
        String cmd = (String) request.getCommand();
        EzyData data = request.serialize();
        send(cmd, data);
    }
    
    @Override
    public void send(EzyArray request) {
    	send(request, false);
    }
    
    @Override
    public void send(EzyArray request, boolean encrypted) {
    	EzyArray requestData = EzyEntityFactory.newArray();
        requestData.add(id, request);
    	client.send(EzyCommand.APP_REQUEST, requestData, encrypted);
    }

    @Override
    public void send(String cmd) {
        send(cmd, EzyEntityFactory.EMPTY_OBJECT);
    }

    @Override
    public void send(String cmd, EzyData data) {
    	send(cmd, data, false);
    }
    
    @Override
    public void send(String cmd, EzyData data, boolean encrypted) {
    	EzyArray commandData = EzyEntityFactory.newArray();
    	commandData.add(cmd, data);
    	send(commandData, encrypted);
    }
    
    @Override
    public void udpSend(EzyRequest request) {
        String cmd = (String) request.getCommand();
        EzyData data = request.serialize();
        udpSend(cmd, data);
    }

    @Override
    public void udpSend(String cmd) {
        udpSend(cmd, EzyEntityFactory.EMPTY_OBJECT);
    }

    @Override
    public void udpSend(String cmd, EzyData data) {
    	EzyArray commandData = EzyEntityFactory.newArray();
    	commandData.add(cmd, data);
        EzyArray requestData = EzyEntityFactory.newArray();
        requestData.add(id, commandData);
        client.udpSend(EzyCommand.APP_REQUEST, requestData);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EzyClient getClient() {
        return client;
    }

    @Override
    public EzyZone getZone() {
        return zone;
    }

    @Override
    public EzyAppDataHandler<?> getDataHandler(Object cmd) {
        EzyAppDataHandler<?> handler = dataHandlers.getHandler(cmd);
        return handler;
    }
    
    @Override
    public boolean equals(Object obj) {
    	return new EzyEquals<EzySimpleApp>()
    			.function(t -> t.id)
    			.isEquals(this, obj);
    }
    
    @Override
    public int hashCode() {
    	return id;
    }
    
    @Override
    public String toString() {
    	return new StringBuilder()
    			.append("App(")
    			.append("id: ").append(id).append(", ")
    			.append("name: ").append(name)
    			.append(")")
    			.toString();
    }
}
