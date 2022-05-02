package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;

public interface EzyPluginByIdGroup {

    void addPlugin(EzyPlugin app);

    EzyPlugin removePlugin(int appId);

    EzyPlugin getPluginById(int appId);
}
