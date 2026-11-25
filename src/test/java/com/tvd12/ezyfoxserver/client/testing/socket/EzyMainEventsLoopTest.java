package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;
import com.tvd12.ezyfoxserver.client.socket.EzyMainEventsLoop;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class EzyMainEventsLoopTest {

    @BeforeMethod
    public void before() {
        EzyClients.getInstance().clear();
    }

    @AfterMethod
    public void after() {
        EzyClients.getInstance().clear();
    }

    @Test
    public void startAndStopTest() throws Exception {
        // given
        CountDownLatch processedLatch = new CountDownLatch(2);
        EzyClient client = mock(EzyClient.class);
        when(client.getName()).thenReturn(RandomUtil.randomShortAlphabetString());
        doAnswer(invocation -> {
            processedLatch.countDown();
            return null;
        }).when(client).processEvents();
        EzyClients.getInstance().addClient(client);
        EzyMainEventsLoop sut = new EzyMainEventsLoop();
        Thread thread = new Thread(sut::start);

        // when
        thread.start();

        // then
        assert processedLatch.await(3, TimeUnit.SECONDS);
        sut.stop();
        thread.join(3000);
        assert !thread.isAlive();
    }

    @Test
    public void processEventsErrorTest() throws Exception {
        // given
        CountDownLatch processedLatch = new CountDownLatch(2);
        EzyClient client = mock(EzyClient.class);
        when(client.getName()).thenReturn(RandomUtil.randomShortAlphabetString());
        doAnswer(invocation -> {
            processedLatch.countDown();
            throw new IllegalStateException("test");
        }).when(client).processEvents();
        EzyClients.getInstance().addClient(client);
        EzyMainEventsLoop sut = new EzyMainEventsLoop();
        Thread thread = new Thread(() -> sut.start(1));

        // when
        thread.start();

        // then
        assert processedLatch.await(3, TimeUnit.SECONDS);
        sut.stop();
        thread.join(3000);
        assert !thread.isAlive();
    }
}
