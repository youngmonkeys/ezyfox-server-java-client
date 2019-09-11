package com.tvd12.ezyfoxserver.client.setup;

import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;

/**
 * Created by tavandung12 on 10/3/18.
 */

@SuppressWarnings("rawtypes")
public interface EzyAppSetup {

	EzyAppSetup addDataHandler(Object cmd, EzyAppDataHandler dataHandler);

    EzySetup done();

}
