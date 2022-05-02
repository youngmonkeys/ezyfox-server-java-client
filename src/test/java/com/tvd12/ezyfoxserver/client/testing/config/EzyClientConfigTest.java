package com.tvd12.ezyfoxserver.client.testing.config;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.config.EzyPingConfig;
import org.testng.annotations.Test;

public class EzyClientConfigTest {

    @Test
    public void propertiesTest() {
        // given
        EzyClientConfig clientConfig = EzyClientConfig.builder()
            .pingConfigBuilder()
            .done()
            .build();

        // when
        EzyPingConfig actualPingConfig = clientConfig.getPing();

        // then
        assert actualPingConfig != null;
    }
}
