package com.tvd12.ezyfoxserver.client.constant;

import com.tvd12.ezyfox.constant.EzyConstant;

public enum EzyTransportType implements EzyConstant {

    TCP(1),
    UDP(2);

    private final int id;

    EzyTransportType(int id) {
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
