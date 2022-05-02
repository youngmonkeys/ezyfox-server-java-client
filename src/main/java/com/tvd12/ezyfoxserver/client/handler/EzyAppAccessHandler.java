package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.entity.EzySimpleApp;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;

public class EzyAppAccessHandler extends EzyAbstractDataHandler {

    @Override
    public void handle(EzyArray data) {
        EzyZone zone = client.getZone();
        EzyAppManager appManager = zone.getAppManager();
        EzyApp app = newApp(zone, data);
        appManager.addApp(app);
        postHandle(app, data);
    }

    protected void postHandle(EzyApp app, EzyArray data) {
    }

    protected EzyApp newApp(EzyZone zone, EzyArray data) {
        int appId = data.get(0, int.class);
        String appName = data.get(1, String.class);
        return new EzySimpleApp(zone, appId, appName);
    }
}
