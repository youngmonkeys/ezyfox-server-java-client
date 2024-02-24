package com.tvd12.ezyfoxserver.client.testing.stresstest.setup;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;

public class ClientConfigFactory {

    public EzyClientConfig.Builder newConfigBuilder(
        int index
    ) {
        return EzyClientConfig.builder()
            .clientName("client-" + index)
            .zoneName("example");
    }
}
