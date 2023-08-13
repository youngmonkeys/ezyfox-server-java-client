package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.request.EzyPingRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzySocketEventQueue;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyPingScheduleTest extends BaseTest {

    @Test
    public void start() throws Exception {
        // given
        long pingPeriod = 30L;
        int maxLostPingCount = 100;
        EzyClient client = mock(EzyClient.class);

        EzyPingManager pingManager = mock(EzyPingManager.class);
        when(pingManager.getPingPeriod()).thenReturn(pingPeriod);
        when(pingManager.getMaxLostPingCount()).thenReturn(maxLostPingCount);
        when(pingManager.increaseLostPingCount()).thenReturn(2);

        when(client.getPingManager()).thenReturn(pingManager);

        EzySocketEventQueue socketEventQueue = new EzySocketEventQueue();

        EzyPingSchedule sut = new EzyPingSchedule(client);
        sut.setSocketEventQueue(socketEventQueue);

        // when
        // then
        sut.start();

        Thread.sleep(pingPeriod * 5);
        sut.stop();
        sut.stop();
    }

    @Test
    public void lostPingCountGreaterThanMaxLostPingCount() throws Exception {
        // given
        long pingPeriod = 30L;
        int maxLostPingCount = 1;
        EzyClient client = mock(EzyClient.class);

        EzyPingManager pingManager = mock(EzyPingManager.class);
        when(pingManager.getPingPeriod()).thenReturn(pingPeriod);
        when(pingManager.getMaxLostPingCount()).thenReturn(maxLostPingCount);
        when(pingManager.increaseLostPingCount()).thenReturn(2);

        when(client.getPingManager()).thenReturn(pingManager);

        EzySocketEventQueue socketEventQueue = new EzySocketEventQueue();

        EzyPingSchedule sut = new EzyPingSchedule(client);
        sut.setSocketEventQueue(socketEventQueue);

        // when
        // then
        sut.start();

        Thread.sleep(pingPeriod * 5);
        sut.stop();
        sut.stop();
    }

    @Test
    public void lostPingCountEqual1() throws Exception {
        // given
        long pingPeriod = 30L;
        int maxLostPingCount = 100;
        EzyClient client = mock(EzyClient.class);

        EzyPingManager pingManager = mock(EzyPingManager.class);
        when(pingManager.getPingPeriod()).thenReturn(pingPeriod);
        when(pingManager.getMaxLostPingCount()).thenReturn(maxLostPingCount);
        when(pingManager.increaseLostPingCount()).thenReturn(1);

        when(client.getPingManager()).thenReturn(pingManager);

        EzySocketEventQueue socketEventQueue = new EzySocketEventQueue();

        EzyPingSchedule sut = new EzyPingSchedule(client);
        sut.setSocketEventQueue(socketEventQueue);

        // when
        // then
        sut.start();

        Thread.sleep(pingPeriod * 5);
        sut.stop();
        sut.stop();
    }

    @Test
    public void pingFailed() throws Exception {
        // given
        long pingPeriod = 30L;
        int maxLostPingCount = 100;
        EzyClient client = mock(EzyClient.class);

        EzyPingManager pingManager = mock(EzyPingManager.class);
        when(pingManager.getPingPeriod()).thenReturn(pingPeriod);
        when(pingManager.getMaxLostPingCount()).thenReturn(maxLostPingCount);
        when(pingManager.increaseLostPingCount()).thenReturn(1);

        when(client.getPingManager()).thenReturn(pingManager);

        doThrow(new RuntimeException("just test"))
            .when(client)
            .send(EzyPingRequest.getInstance());

        EzySocketEventQueue socketEventQueue = new EzySocketEventQueue();

        EzyPingSchedule sut = new EzyPingSchedule(client);
        sut.setSocketEventQueue(socketEventQueue);

        // when
        // then
        sut.start();

        Thread.sleep(pingPeriod * 5);
        sut.stop();
        sut.stop();
    }
}
