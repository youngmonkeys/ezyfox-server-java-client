package com.tvd12.ezyfoxserver.client.config;

public interface EzySocketClientConfig {

    EzySocketClientConfig DEFAULT = () -> false;

    boolean isEnableSSL();
}
