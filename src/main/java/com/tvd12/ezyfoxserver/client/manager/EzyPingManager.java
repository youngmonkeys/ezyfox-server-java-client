package com.tvd12.ezyfoxserver.client.manager;

public interface EzyPingManager {

    long getPingPeriod();

    int increaseLostPingCount();

    int getMaxLostPingCount();

    void setLostPingCount(int count);
}
