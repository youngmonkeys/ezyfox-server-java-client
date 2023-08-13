package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyStatusCodes;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketConnector;

public class EzyUdpHandshakeHandler extends EzyAbstractDataHandler {

    @Override
    public final void handle(EzyArray data) {
        int responseCode = data.get(0, int.class);
        EzyUTClient utClient = (EzyUTClient) client;
        EzyUdpSocketConnector socket =
            (EzyUdpSocketConnector) client.getSocket();
        if (responseCode == EzyStatusCodes.OK) {
            utClient.setUdpStatus(EzyConnectionStatus.CONNECTED);
            socket.udpSetStatus(EzySocketStatus.CONNECTED);
            onAuthenticated(data);
        } else {
            utClient.setUdpStatus(EzyConnectionStatus.FAILURE);
            socket.udpSetStatus(EzySocketStatus.CONNECT_FAILED);
            onAuthenticationError(data);
        }
    }

    protected void onAuthenticated(EzyArray data) {
        logger.info("udp authenticated");
    }

    protected void onAuthenticationError(EzyArray data) {
        int responseCode = data.get(0, int.class);
        logger.info("udp authentication error: " + responseCode);
    }
}
