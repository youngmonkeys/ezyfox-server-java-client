package com.tvd12.ezyfoxserver.client.manager;

import java.util.List;

import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;

public interface EzyPluginGroup extends EzyPluginByIdGroup {

    EzyPlugin getPlugin();

    List<EzyPlugin> getPluginList();

    EzyPlugin getPluginByName(String appName);

}
