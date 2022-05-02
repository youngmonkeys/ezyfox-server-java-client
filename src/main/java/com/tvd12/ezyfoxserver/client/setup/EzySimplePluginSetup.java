package com.tvd12.ezyfoxserver.client.setup;

import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandlers;

@SuppressWarnings("rawtypes")
public class EzySimplePluginSetup implements EzyPluginSetup {

    private final EzyPluginDataHandlers dataHandlers;
    private final EzySetup parent;

    public EzySimplePluginSetup(EzyPluginDataHandlers dataHandlers, EzySetup parent) {
        this.parent = parent;
        this.dataHandlers = dataHandlers;
    }

    @Override
    public EzyPluginSetup addDataHandler(Object cmd, EzyPluginDataHandler dataHandler) {
        dataHandlers.addHandler(cmd, dataHandler);
        return this;
    }

    @Override
    public EzySetup done() {
        return parent;
    }
}
