package com.tvd12.ezyfoxserver.client.testing.manager;

import com.tvd12.ezyfoxserver.client.config.EzyPingConfig;
import com.tvd12.ezyfoxserver.client.manager.EzySimplePingManager;
import org.testng.annotations.Test;

import java.util.Random;

public class EzySimplePingManagerTest {

    @Test
    public void propertiesTest() {
        // given
        long pingPeriod = new Random().nextInt();
        int lostPingCount = new Random().nextInt();
        int maxLostPingCount = new Random().nextInt();
        EzyPingConfig pingConfig = new EzyPingConfig.Builder(null)
            .pingPeriod(pingPeriod)
            .maxLostPingCount(maxLostPingCount)
            .build();
        EzySimplePingManager sut = new EzySimplePingManager(pingConfig);
        sut.setLostPingCount(lostPingCount);

        // when
        long actualPingPeriod = sut.getPingPeriod();
        int actualLostPingCount = sut.increaseLostPingCount();
        int actualMaxLostPingCount = sut.getMaxLostPingCount();

        // then
        assert actualPingPeriod == pingPeriod;
        assert actualLostPingCount == lostPingCount + 1;
        assert actualMaxLostPingCount == maxLostPingCount;
    }
}
