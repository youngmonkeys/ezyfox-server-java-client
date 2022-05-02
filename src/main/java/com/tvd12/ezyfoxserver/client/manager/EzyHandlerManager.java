package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.handler.*;

@SuppressWarnings("rawtypes")
public interface EzyHandlerManager {

    EzyEventHandlers getEventHandlers();

    EzyDataHandlers getDataHandlers();

    EzyDataHandler getDataHandler(Object cmd);

    EzyEventHandler getEventHandler(EzyConstant eventType);

    void addDataHandler(Object cmd, EzyDataHandler dataHandler);

    void addEventHandler(EzyConstant eventType, EzyEventHandler eventHandler);

    EzyAppDataHandlers getAppDataHandlers(String appName);

    EzyPluginDataHandlers getPluginDataHandlers(String pluginName);
}
