package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EzySimplePluginManager implements EzyPluginManager {

    protected final String zoneName;
    protected final List<EzyPlugin> pluginList;
    protected final Map<Integer, EzyPlugin> pluginsById;
    protected final Map<String, EzyPlugin> pluginsByName;

    public EzySimplePluginManager(String zoneName) {
        this.zoneName = zoneName;
        this.pluginList = new ArrayList<>();
        this.pluginsById = new HashMap<>();
        this.pluginsByName = new HashMap<>();
    }

    @Override
    public void addPlugin(EzyPlugin plugin) {
        synchronized (this) {
            this.pluginList.add(plugin);
            this.pluginsById.put(plugin.getId(), plugin);
            this.pluginsByName.put(plugin.getName(), plugin);
        }
    }

    @Override
    public EzyPlugin removePlugin(int pluginId) {
        synchronized (this) {
            EzyPlugin plugin = this.pluginsById.remove(pluginId);
            if (plugin != null) {
                this.pluginList.remove(plugin);
                this.pluginsByName.remove(plugin.getName());
            }
            return plugin;
        }
    }

    @Override
    public EzyPlugin getPlugin() {
        synchronized (this) {
            return pluginList.isEmpty() ? null : pluginList.get(0);
        }
    }

    @Override
    public List<EzyPlugin> getPluginList() {
        List<EzyPlugin> list = new ArrayList<>();
        synchronized (this) {
            //noinspection CollectionAddAllCanBeReplacedWithConstructor
            list.addAll(pluginList);
        }
        return list;
    }

    @Override
    public EzyPlugin getPluginById(int pluginId) {
        synchronized (pluginList) {
            return pluginsById.get(pluginId);
        }
    }

    @Override
    public EzyPlugin getPluginByName(String pluginName) {
        synchronized (this) {
            return pluginsByName.get(pluginName);
        }
    }

    @Override
    public void clear() {
        synchronized (this) {
            pluginList.clear();
            pluginsById.clear();
            pluginsByName.clear();
        }
    }
}
