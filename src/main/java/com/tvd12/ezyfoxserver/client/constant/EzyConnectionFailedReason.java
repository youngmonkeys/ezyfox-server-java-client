package com.tvd12.ezyfoxserver.client.constant;

import com.tvd12.ezyfox.constant.EzyConstant;

public enum EzyConnectionFailedReason implements EzyConstant {

    NETWORK_UNREACHABLE(1),
    UNKNOWN_HOST(2),
    CONNECTION_REFUSED(3),
    UNKNOWN(4);

    private final int id;

    EzyConnectionFailedReason(int id) {
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
