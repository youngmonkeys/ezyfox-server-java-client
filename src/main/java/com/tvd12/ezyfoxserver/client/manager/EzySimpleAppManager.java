package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfoxserver.client.entity.EzyApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EzySimpleAppManager implements EzyAppManager {

    protected final String zoneName;
    protected final List<EzyApp> appList;
    protected final Map<Integer, EzyApp> appsById;
    protected final Map<String, EzyApp> appsByName;

    public EzySimpleAppManager(String zoneName) {
        this.zoneName = zoneName;
        this.appList = new ArrayList<>();
        this.appsById = new HashMap<>();
        this.appsByName = new HashMap<>();
    }

    @Override
    public void addApp(EzyApp app) {
        synchronized (this) {
            this.appList.add(app);
            this.appsById.put(app.getId(), app);
            this.appsByName.put(app.getName(), app);
        }
    }

    @Override
    public EzyApp removeApp(int appId) {
        synchronized (this) {
            EzyApp app = this.appsById.remove(appId);
            if (app != null) {
                this.appList.remove(app);
                this.appsByName.remove(app.getName());
            }
            return app;
        }
    }

    @Override
    public EzyApp getApp() {
        synchronized (this) {
            return appList.isEmpty() ? null : appList.get(0);
        }
    }

    @Override
    public List<EzyApp> getAppList() {
        List<EzyApp> list = new ArrayList<>(appList.size());
        synchronized (this) {
            list.addAll(appList);
        }
        return list;
    }

    @Override
    public EzyApp getAppById(int appId) {
        synchronized (appList) {
            EzyApp app = appsById.get(appId);
            return app;
        }
    }

    @Override
    public EzyApp getAppByName(String appName) {
        synchronized (this) {
            EzyApp app = appsByName.get(appName);
            return app;
        }
    }

    @Override
    public void clear() {
        synchronized (this) {
            appList.clear();
            appsById.clear();
            appsByName.clear();
        }
    }
}
