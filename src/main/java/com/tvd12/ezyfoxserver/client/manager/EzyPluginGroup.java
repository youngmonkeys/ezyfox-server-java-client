package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;

import java.util.List;

public interface EzyPluginGroup extends EzyPluginByIdGroup {

    EzyPlugin getPlugin();

    List<EzyPlugin> getPluginList();

    EzyPlugin getPluginByName(String appName);
}
