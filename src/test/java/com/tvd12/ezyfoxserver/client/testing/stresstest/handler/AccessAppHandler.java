package com.tvd12.ezyfoxserver.client.testing.stresstest.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyObject;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.handler.EzyAppAccessHandler;
import lombok.AllArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
public class AccessAppHandler extends EzyAppAccessHandler {

    private final boolean useUdp;
    private final AtomicLong messageCount;
    private final ScheduledExecutorService executorService;

    @Override
    protected void postHandle(EzyApp app, EzyArray data) {
        executorService.scheduleAtFixedRate(() -> sendMessage(app), 1, 1, TimeUnit.SECONDS);
    }

    private void sendMessage(EzyApp app) {
        if (!app.getClient().isConnected()) {
            return;
        }
        if (useUdp) {
            app.udpSend("udpBroadcastMessage", newMessageData());
        } else if (client.isEnableEncryption()) {
            app.send("broadcastSecureMessage", newMessageData(), true);
        } else {
            app.send("broadcastMessage", newMessageData());
        }
    }

    private EzyObject newMessageData() {
        return EzyEntityFactory.newObjectBuilder()
            .append("message", "Message#" + messageCount.incrementAndGet())
            .build();
    }
}
