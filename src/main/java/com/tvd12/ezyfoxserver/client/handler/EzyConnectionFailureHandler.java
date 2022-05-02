package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.config.EzyReconnectConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.event.EzyConnectionFailureEvent;

public class EzyConnectionFailureHandler
    extends EzyAbstractEventHandler<EzyConnectionFailureEvent> {

    @Override
    public final void handle(EzyConnectionFailureEvent event) {
        logger.info("connection failure, reason: {}", event.getReason());
        EzyClientConfig config = client.getConfig();
        EzyReconnectConfig reconnectConfig = config.getReconnect();
        boolean shouldReconnect = shouldReconnect(event);
        boolean mustReconnect = reconnectConfig.isEnable() && shouldReconnect;
        boolean reconnecting = false;
        client.setStatus(EzyConnectionStatus.FAILURE);
        if (mustReconnect) {
            reconnecting = client.reconnect();
        }
        if (reconnecting) {
            onReconnecting(event);
        } else {
            onConnectionFailed(event);
        }
        postHandle(event);
    }

    protected boolean shouldReconnect(EzyConnectionFailureEvent event) {
        return true;
    }

    protected void onReconnecting(EzyConnectionFailureEvent event) {}

    protected void onConnectionFailed(EzyConnectionFailureEvent event) {}

    protected void postHandle(EzyConnectionFailureEvent event) {}
}
