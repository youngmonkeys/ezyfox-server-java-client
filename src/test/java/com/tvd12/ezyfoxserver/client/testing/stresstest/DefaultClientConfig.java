package com.tvd12.ezyfoxserver.client.testing.stresstest;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;

public class DefaultClientConfig {

    public EzyClientConfig.Builder newClientConfigBuilder(
        int index
    ) {
        return EzyClientConfig.builder()
            .clientName("hello-word-" + index)
            .zoneName("example");
    }
}
