package com.tvd12.ezyfoxserver.client.event;

import com.tvd12.ezyfox.constant.EzyConstant;

public enum EzyEventType implements EzyConstant {

    CONNECTION_SUCCESS(1),
    CONNECTION_FAILURE(2),
    DISCONNECTION(3),
    LOST_PING(4),
    TRY_CONNECT(5);

    private final int id;

    EzyEventType(int id) {
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
