package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionFailedReason;
import com.tvd12.ezyfoxserver.client.event.EzyConnectionFailureEvent;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionFailureHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyConnectionFailureHandlerTest {

    @Test
    public void handleMustReconnect() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(true)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyConnectionFailureHandler sut = new EzyConnectionFailureHandler();
        sut.setClient(client);
        EzyConnectionFailedReason reason = EzyConnectionFailedReason.CONNECTION_REFUSED;
        EzyConnectionFailureEvent event = new EzyConnectionFailureEvent(reason);

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
        verify(client, times(1)).reconnect();
    }

    @Test
    public void handleMustReconnectReconnecting() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(true)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        when(client.reconnect()).thenReturn(true);
        EzyConnectionFailureHandler sut = new EzyConnectionFailureHandler();
        sut.setClient(client);
        EzyConnectionFailedReason reason = EzyConnectionFailedReason.CONNECTION_REFUSED;
        EzyConnectionFailureEvent event = new EzyConnectionFailureEvent(reason);

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
        verify(client, times(1)).reconnect();
    }

    @Test
    public void handleShouldNotReconnect() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(true)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyConnectionFailureHandler sut = new EzyConnectionFailureHandler() {
            protected boolean shouldReconnect(EzyConnectionFailureEvent event) {
                return false;
            }
        };
        sut.setClient(client);
        EzyConnectionFailedReason reason = EzyConnectionFailedReason.CONNECTION_REFUSED;
        EzyConnectionFailureEvent event = new EzyConnectionFailureEvent(reason);

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
    }

    @Test
    public void handleReconnectIsNotEnable() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(false)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyConnectionFailureHandler sut = new EzyConnectionFailureHandler();
        sut.setClient(client);
        EzyConnectionFailedReason reason = EzyConnectionFailedReason.CONNECTION_REFUSED;
        EzyConnectionFailureEvent event = new EzyConnectionFailureEvent(reason);

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
    }
}
