package com.tvd12.ezyfoxserver.client.handler;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class EzyAppDataHandlers {

    private final Map<Object, EzyAppDataHandler> handlers;

    public EzyAppDataHandlers() {
        this.handlers = new HashMap<>();
    }

    public void addHandler(Object cmd, EzyAppDataHandler handler) {
        handlers.put(cmd, handler);
    }

    public EzyAppDataHandler getHandler(Object cmd) {
        return handlers.get(cmd);
    }
}
