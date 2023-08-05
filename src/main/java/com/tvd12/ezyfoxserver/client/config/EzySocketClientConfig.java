package com.tvd12.ezyfoxserver.client.config;

import com.tvd12.ezyfoxserver.client.constant.EzySslType;

public interface EzySocketClientConfig {

    EzySocketClientConfig DEFAULT = new EzySocketClientConfig() {};

    default boolean isEnableSSL() {
        return false;
    }

    default EzySslType getSslType() {
        return EzySslType.CUSTOMIZATION;
    }
}
