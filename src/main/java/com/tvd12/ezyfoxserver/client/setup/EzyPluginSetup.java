package com.tvd12.ezyfoxserver.client.setup;

import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandler;

@SuppressWarnings("rawtypes")
public interface EzyPluginSetup {

    EzyPluginSetup addDataHandler(Object cmd, EzyPluginDataHandler dataHandler);

    EzySetup done();
}
