package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandlers;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyEventHandlersTest {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void handleWithHandler() {
        // given
        EzyClient client = mock(EzyClient.class);
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyEventHandlers sut = new EzyEventHandlers(client, pingSchedule);

        EzyEventHandler eventHandler = mock(EzyEventHandler.class);
        sut.addHandler(EzyEventType.CONNECTION_SUCCESS, eventHandler);

        EzyEvent event = mock(EzyEvent.class);
        when(event.getType()).thenReturn(EzyEventType.CONNECTION_SUCCESS);

        // when
        sut.handle(event);

        // then
        verify(event, times(1)).getType();
        verify(eventHandler, times(1)).handle(event);
    }

    @SuppressWarnings({"rawtypes"})
    @Test
    public void handleWithNoHandler() {
        // given
        EzyClient client = mock(EzyClient.class);
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyEventHandlers sut = new EzyEventHandlers(client, pingSchedule);

        EzyEventHandler eventHandler = mock(EzyEventHandler.class);
        sut.addHandler(EzyEventType.CONNECTION_SUCCESS, eventHandler);

        EzyEvent event = mock(EzyEvent.class);
        when(event.getType()).thenReturn(EzyEventType.DISCONNECTION);

        // when
        sut.handle(event);

        // then
        verify(event, times(1)).getType();
    }
}
