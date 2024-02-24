package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.request.EzyPingRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzySocketEventQueue;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.util.RandomUtil;
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

        EzyPingSchedule sut = new EzyPingSchedule(client, null);
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

        EzyPingSchedule sut = new EzyPingSchedule(client, null);
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

        EzyPingSchedule sut = new EzyPingSchedule(client, null);
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

        EzyPingSchedule sut = new EzyPingSchedule(client, null);
        sut.setSocketEventQueue(socketEventQueue);

        // when
        // then
        sut.start();

        Thread.sleep(pingPeriod * 5);
        sut.stop();
        sut.stop();
    }

    @Test
    public void startStopWithEventLoopGroup() {
        // given
        EzyClient client = mock(EzyClient.class);

        EzySocketClient socket = mock(EzySocketClient.class);
        when(client.getSocket()).thenReturn(socket);

        EzyPingManager pingManager = mock(EzyPingManager.class);
        long periodMillis = RandomUtil.randomSmallInt();
        when(pingManager.getPingPeriod()).thenReturn(periodMillis);
        when(client.getPingManager()).thenReturn(pingManager);

        doThrow(new RuntimeException("just test"))
            .when(client)
            .send(EzyPingRequest.getInstance());

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        EzyPingSchedule sut = new EzyPingSchedule(client, eventLoopGroup);

        EzySocketEventQueue socketEventQueue = new EzySocketEventQueue();
        sut.setSocketEventQueue(socketEventQueue);

        // when
        sut.start();
        sut.call();
        sut.stop();

        // then
        verify(eventLoopGroup, times(1))
            .addScheduleEvent(
                sut,
                periodMillis,
                periodMillis
            );
        verify(eventLoopGroup, times(1))
            .removeEvent(sut);
    }
}
