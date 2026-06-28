package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;
import com.tvd12.ezyfoxserver.client.entity.EzySimplePlugin;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.manager.EzyPluginManager;

public class EzyPluginInfoHandler extends EzyAbstractDataHandler {

    @Override
    public void handle(EzyArray data) {
        EzyZone zone = client.getZone();
        EzyPluginManager pluginManager = zone.getPluginManager();
        EzyPlugin plugin = newPlugin(zone, data);
        pluginManager.addPlugin(plugin);
        this.postHandle(plugin, data);
    }

    protected void postHandle(EzyPlugin plugin, EzyArray data) {
    }

    protected EzyPlugin newPlugin(EzyZone zone, EzyArray data) {
        int pluginId = data.get(0, int.class);
        String pluginName = data.get(1, String.class);
        return new EzySimplePlugin(zone, pluginId, pluginName);
    }
}
