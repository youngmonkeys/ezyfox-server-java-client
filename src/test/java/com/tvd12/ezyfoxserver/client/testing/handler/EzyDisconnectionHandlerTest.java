package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.event.EzyDisconnectionEvent;
import com.tvd12.ezyfoxserver.client.handler.EzyDisconnectionHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyDisconnectionHandlerTest {

    @Test
    public void handleMustReconnectNotReconnecting() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(true)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyDisconnectionHandler sut = new EzyDisconnectionHandler();
        sut.setClient(client);
        EzyDisconnectReason reason = EzyDisconnectReason.NOT_LOGGED_IN;
        EzyDisconnectionEvent event = new EzyDisconnectionEvent(reason.getId());

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
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
        EzyDisconnectionHandler sut = new EzyDisconnectionHandler();
        sut.setClient(client);
        EzyDisconnectReason reason = EzyDisconnectReason.NOT_LOGGED_IN;
        EzyDisconnectionEvent event = new EzyDisconnectionEvent(reason.getId());

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
    }

    @Test
    public void handleMustReconnectIsNotEnable() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(false)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyDisconnectionHandler sut = new EzyDisconnectionHandler();
        sut.setClient(client);
        EzyDisconnectReason reason = EzyDisconnectReason.NOT_LOGGED_IN;
        EzyDisconnectionEvent event = new EzyDisconnectionEvent(reason.getId());

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
    }

    @Test
    public void handleAnotherSessionLogin() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(true)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyDisconnectionHandler sut = new EzyDisconnectionHandler();
        sut.setClient(client);
        EzyDisconnectReason reason = EzyDisconnectReason.ANOTHER_SESSION_LOGIN;
        EzyDisconnectionEvent event = new EzyDisconnectionEvent(reason.getId());

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
    }

    @Test
    public void handleClientClose() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .reconnectConfigBuilder()
            .enable(true)
            .done()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyDisconnectionHandler sut = new EzyDisconnectionHandler();
        sut.setClient(client);
        EzyDisconnectReason reason = EzyDisconnectReason.CLOSE;
        EzyDisconnectionEvent event = new EzyDisconnectionEvent(reason.getId());

        // when
        sut.handle(event);

        // then
        verify(client, times(1)).getConfig();
    }

}
