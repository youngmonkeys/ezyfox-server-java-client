package com.tvd12.ezyfoxserver.client.socket;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.event.EzyLostPingEvent;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.request.EzyPingRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;

/**
 * Created by tavandung12 on 10/2/18.
 */

public class EzyPingSchedule extends EzyLoggable {

	private final EzyClient client;
    private final EzyPingManager pingManager;
    private EzySocketDataHandler dataHandler;
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledExecutorService scheduledExecutor;

    public EzyPingSchedule(EzyClient client) {
    		this.client = client;
        this.pingManager = client.getPingManager();
        this.scheduledExecutor = EzyExecutors.newSingleThreadScheduledExecutor("ping-schedule");
        Runtime.getRuntime().addShutdownHook(new Thread(scheduledExecutor::shutdown));

    }

    public void start() {
    		long periodMillis = pingManager.getPingPeriod();
        scheduledFuture = scheduledExecutor.scheduleAtFixedRate(
        		this::sendPingRequest, periodMillis, periodMillis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if(scheduledFuture != null)
        		this.scheduledFuture.cancel(true);
        this.scheduledFuture = null;
    }

    private void sendPingRequest() {
        int lostPingCount = pingManager.increaseLostPingCount();
        int maxLostPingCount = pingManager.getMaxLostPingCount();
        if(lostPingCount >= maxLostPingCount) {
            dataHandler.fireSocketDisconnected(EzyDisconnectReason.SERVER_NOT_RESPONDING.getId());
        }
        else {
            EzyRequest request = new EzyPingRequest();
            client.send(request);
        }
        if(lostPingCount > 1) {
            logger.info("lost ping count: {}", lostPingCount);
            EzyLostPingEvent event = new EzyLostPingEvent(lostPingCount);
            EzySocketEvent socketEvent = new EzySimpleSocketEvent(EzySocketEventType.EVENT, event);
            dataHandler.fireSocketEvent(socketEvent);
        }
    }

    public void setDataHandler(EzySocketDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
}
