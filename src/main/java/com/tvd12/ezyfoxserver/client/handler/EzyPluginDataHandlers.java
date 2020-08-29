package com.tvd12.ezyfoxserver.client.handler;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class EzyPluginDataHandlers {

	private final Map<Object, EzyPluginDataHandler> handlers;

    public EzyPluginDataHandlers() {
        this.handlers = new HashMap<>();
    }

    public void addHandler(Object cmd, EzyPluginDataHandler handler) {
        handlers.put(cmd, handler);
    }

    public EzyPluginDataHandler getHandler(Object cmd) {
    	EzyPluginDataHandler handler = handlers.get(cmd);
        return handler;
    }

}
