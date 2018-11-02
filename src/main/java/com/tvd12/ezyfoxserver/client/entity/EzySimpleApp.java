package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.builder.EzyArrayBuilder;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.entity.EzyEntity;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;

import lombok.Getter;

/**
 * Created by tavandung12 on 10/2/18.
 */

@Getter
public class EzySimpleApp extends EzyEntity implements EzyApp {

	protected final int id;
    protected final String name;
    protected final EzyClient client;
    protected final EzyZone zone;
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
        Object cmd = request.getCommand();
        EzyData data = request.serialize();
        send(cmd, data);
    }

    @Override
    public void send(Object cmd, EzyData data) {
        EzyArrayBuilder commandData = EzyEntityFactory.newArrayBuilder()
                .append(cmd)
                .append(data);
        EzyData requestData = EzyEntityFactory.newArrayBuilder()
                .append(id)
                .append(commandData)
                .build();
        client.send(EzyCommand.APP_REQUEST, requestData);
    }

    @Override
    public <T> T get(Class<T> clazz) {
        T instance = getProperty(clazz);
        return instance;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public EzyAppDataHandler getDataHandler(Object cmd) {
        EzyAppDataHandler handler = dataHandlers.getHandler(cmd);
        return handler;
    }

    @Override
    public void destroy() {
    }
}
