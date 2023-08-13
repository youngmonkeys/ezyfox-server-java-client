package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.event.EzyLostPingEvent;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.request.EzyPingRequest;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class EzyPingSchedule extends EzyLoggable {

    protected final EzyClient client;
    protected final EzyPingManager pingManager;
    protected final ScheduledExecutorService scheduledExecutor;
    protected ScheduledFuture<?> scheduledFuture;
    protected EzySocketEventQueue socketEventQueue;

    public EzyPingSchedule(EzyClient client) {
        this.client = client;
        this.pingManager = client.getPingManager();
        this.scheduledExecutor = newScheduledExecutor();

    }

    protected ScheduledExecutorService newScheduledExecutor() {
        final ScheduledExecutorService answer = EzyExecutors
            .newSingleThreadScheduledExecutor("ping-schedule");
        Runtime.getRuntime().addShutdownHook(new Thread(answer::shutdown));
        return answer;
    }

    public void start() {
        synchronized (this) {
            long periodMillis = pingManager.getPingPeriod();
            scheduledFuture = scheduledExecutor.scheduleAtFixedRate(
                () -> {
                    try {
                        sendPingRequest();
                    } catch (Throwable e) {
                        logger.info("send ping request failed", e);
                    }
                },
                periodMillis,
                periodMillis, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        synchronized (this) {
            if (scheduledFuture != null) {
                this.scheduledFuture.cancel(true);
            }
            this.scheduledFuture = null;
        }
    }

    public void shutdown() {
        this.scheduledExecutor.shutdown();
    }

    private void sendPingRequest() {
        int lostPingCount = pingManager.increaseLostPingCount();
        int maxLostPingCount = pingManager.getMaxLostPingCount();
        if (lostPingCount >= maxLostPingCount) {
            client.getSocket().disconnect(EzyDisconnectReason.SERVER_NOT_RESPONDING.getId());
        } else {
            client.send(EzyPingRequest.getInstance());
        }
        if (lostPingCount > 1) {
            logger.info("lost ping count: " + lostPingCount);
            EzyLostPingEvent event = new EzyLostPingEvent(lostPingCount);
            socketEventQueue.addEvent(event);
        }
    }

    public void setSocketEventQueue(EzySocketEventQueue socketEventQueue) {
        this.socketEventQueue = socketEventQueue;
    }
}
