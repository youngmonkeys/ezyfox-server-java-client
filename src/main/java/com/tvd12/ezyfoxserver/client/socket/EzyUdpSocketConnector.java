package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;

public interface EzyUdpSocketConnector {

    void udpConnect(int port);

    void udpConnect(String host, int port);

    void udpSendMessage(EzyArray message, boolean encrypted);

    void udpSetStatus(EzySocketStatus status);
}
