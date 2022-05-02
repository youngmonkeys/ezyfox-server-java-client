package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.util.EzyEquals;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPluginManager;
import com.tvd12.ezyfoxserver.client.manager.EzySimpleAppManager;
import com.tvd12.ezyfoxserver.client.manager.EzySimplePluginManager;

public class EzySimpleZone implements EzyZone {

    protected final int id;
    protected final String name;
    protected final EzyClient client;
    protected final EzyAppManager appManager;
    protected final EzyPluginManager pluginManager;

    public EzySimpleZone(EzyClient client, int id, String name) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.appManager = new EzySimpleAppManager(name);
        this.pluginManager = new EzySimplePluginManager(name);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EzyClient getClient() {
        return client;
    }

    @Override
    public EzyAppManager getAppManager() {
        return appManager;
    }

    @Override
    public EzyPluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public EzyApp getApp() {
        return appManager.getApp();
    }

    @Override
    public EzyPlugin getPlugin() {
        return pluginManager.getPlugin();
    }

    @Override
    public boolean equals(Object obj) {
        return new EzyEquals<EzySimpleZone>()
            .function(t -> t.id)
            .isEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Zone(" +
            "id: " + id + ", " +
            "name: " + name +
            ")";
    }
}
