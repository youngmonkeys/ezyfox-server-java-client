package com.tvd12.ezyfoxserver.client.constant;

import com.tvd12.ezyfox.constant.EzyConstant;

public enum EzyConnectionStatus implements EzyConstant {

    NULL(0),
    CONNECTING(1),
    CONNECTED(2),
    DISCONNECTED(3),
    FAILURE(4),
    RECONNECTING(5);

    private final int id;

    EzyConnectionStatus(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return toString();
    }
}
