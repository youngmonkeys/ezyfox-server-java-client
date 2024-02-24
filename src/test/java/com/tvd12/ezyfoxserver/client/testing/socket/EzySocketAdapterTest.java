package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.socket.EzySocketAdapter;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzySocketAdapterTest {

    @Test
    public void startWithEvenLoopGroupTest() {
        // given
        TestEzySocketAdapter instance = new TestEzySocketAdapter();

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        instance.setEventLoopGroup(eventLoopGroup);

        // when
        instance.start();

        // then
        Asserts.assertTrue(instance.isActive());
        Asserts.assertFalse(instance.isStopped());

        verify(eventLoopGroup, times(1)).addEvent(
            instance
        );
    }

    @Test
    public void startStopTest() {
        // given
        TestEzySocketAdapter instance = new TestEzySocketAdapter();

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        instance.setEventLoopGroup(eventLoopGroup);

        // when
        instance.start();
        instance.start();
        instance.onFinished();
        instance.stop();

        // then
        Asserts.assertFalse(instance.call());
        Asserts.assertFalse(instance.isActive());
        Asserts.assertTrue(instance.isStopped());

        verify(eventLoopGroup, times(1)).addEvent(
            instance
        );

        verify(eventLoopGroup, times(1)).removeEvent(
            instance
        );
    }

    public static class TestEzySocketAdapter extends EzySocketAdapter {

        @Override
        protected String getThreadName() {
            return "test";
        }

        @Override
        protected void update() {
        }
    }
}
