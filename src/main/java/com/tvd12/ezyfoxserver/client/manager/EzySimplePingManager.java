package com.tvd12.ezyfoxserver.client.manager;

import java.util.concurrent.atomic.AtomicInteger;

import com.tvd12.ezyfoxserver.client.config.EzyPingConfig;

/**
 * Created by tavandung12 on 10/8/18.
 */

public class EzySimplePingManager implements EzyPingManager {

    private long pingPeriod;
    private int maxLostPingCount;
    private final AtomicInteger lostPingCount;

    public EzySimplePingManager(EzyPingConfig config) {
        this.pingPeriod = config.getPingPeriod();
        this.maxLostPingCount = config.getMaxLostPingCount();
        this.lostPingCount = new AtomicInteger();
    }

    @Override
    public long getPingPeriod() {
        return pingPeriod;
    }

    @Override
    public void setLostPingCount(int count) {
        lostPingCount.set(count);
    }

    @Override
    public int increaseLostPingCount() {
        return lostPingCount.incrementAndGet();
    }

    @Override
    public int getMaxLostPingCount() {
        return maxLostPingCount;
    }

}
