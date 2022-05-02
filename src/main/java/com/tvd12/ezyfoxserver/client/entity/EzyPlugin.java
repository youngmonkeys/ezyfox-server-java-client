package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandler;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;

public interface EzyPlugin {

    int getId();

    String getName();

    EzyClient getClient();

    EzyZone getZone();

    void send(EzyRequest request);

    void send(String cmd);

    void send(String cmd, EzyData data);

    void send(String cmd, EzyData data, boolean encrypted);

    void udpSend(EzyRequest request);

    void udpSend(String cmd);

    void udpSend(String cmd, EzyData data);

    EzyPluginDataHandler<?> getDataHandler(Object cmd);
}
