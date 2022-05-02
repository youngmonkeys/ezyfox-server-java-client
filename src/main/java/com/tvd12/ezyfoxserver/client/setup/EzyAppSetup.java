package com.tvd12.ezyfoxserver.client.setup;

import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;

@SuppressWarnings("rawtypes")
public interface EzyAppSetup {

    EzyAppSetup addDataHandler(Object cmd, EzyAppDataHandler dataHandler);

    EzySetup done();
}
