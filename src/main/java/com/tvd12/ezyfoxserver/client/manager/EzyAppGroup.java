package com.tvd12.ezyfoxserver.client.manager;

import com.tvd12.ezyfoxserver.client.entity.EzyApp;

import java.util.List;

public interface EzyAppGroup extends EzyAppByIdGroup {

    EzyApp getApp();

    List<EzyApp> getAppList();

    EzyApp getAppByName(String appName);
}
