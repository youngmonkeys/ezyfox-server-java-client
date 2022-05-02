package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfoxserver.client.entity.EzyApp;

public interface EzyAppByIdGroup {

    void addApp(EzyApp app);

    EzyApp removeApp(int appId);

    EzyApp getAppById(int appId);
}
