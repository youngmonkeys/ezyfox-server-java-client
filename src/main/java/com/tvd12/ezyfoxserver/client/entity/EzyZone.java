package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPluginManager;

public interface EzyZone {

    int getId();

    String getName();

    EzyClient getClient();

    EzyAppManager getAppManager();

    EzyPluginManager getPluginManager();

    EzyApp getApp();

    EzyPlugin getPlugin();
}
