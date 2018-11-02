package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;
import com.tvd12.ezyfoxserver.client.manager.EzySimpleAppManager;

import lombok.Getter;

/**
 * Created by tavandung12 on 10/2/18.
 */

@Getter
public class EzySimpleZone implements EzyZone {

    protected final int id;
    protected final String name;
    protected final EzyClient client;
    protected final EzyAppManager appManager;

    public EzySimpleZone(EzyClient client, int id, String name) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.appManager = new EzySimpleAppManager(name);
    }

    @Override
    public void destroy() {
        appManager.clear();
    }
}
