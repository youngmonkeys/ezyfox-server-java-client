package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyConnectionSuccessHandlerTest {

    @Test
    public void handleMustReconnect() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        doNothing().when(client).setStatus(EzyConnectionStatus.CONNECTED);
        EzyConnectionSuccessHandler sut = new EzyConnectionSuccessHandler();
        sut.setClient(client);

        // when
        EzyEvent event = mock(EzyEvent.class);
        sut.handle(event);

        // then
        verify(client, times(1)).setStatus(EzyConnectionStatus.CONNECTED);
    }

    @Test
    public void handleEncryptionEnable() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.isEnableEncryption()).thenReturn(true);
        EzyConnectionSuccessHandler sut = new EzyConnectionSuccessHandler();
        sut.setClient(client);

        // when
        EzyEvent event = mock(EzyEvent.class);
        sut.handle(event);

        // then
        verify(client, times(1)).setStatus(EzyConnectionStatus.CONNECTED);
        verify(client, times(1)).setPublicKey(any());
        verify(client, times(1)).setPrivateKey(any());
    }
}
