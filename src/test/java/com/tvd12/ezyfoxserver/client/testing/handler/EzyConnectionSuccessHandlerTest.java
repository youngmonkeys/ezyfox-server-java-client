package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyClientConstants;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.request.EzyHandshakeRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import com.tvd12.test.assertion.Asserts;
import org.mockito.invocation.Invocation;
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

    @Test
    public void handleWhenConfigNull() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.getConfig()).thenReturn(null);
        EzyConnectionSuccessHandler sut = new EzyConnectionSuccessHandler();
        sut.setClient(client);

        // when
        sut.handle(mock(EzyEvent.class));

        // then
        Asserts.assertFalse(captureEnableCompression(client));
    }

    @Test
    public void handleWhenEnableCompression() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder()
            .enableCompression()
            .build();
        when(client.getConfig()).thenReturn(config);
        EzyConnectionSuccessHandler sut = new EzyConnectionSuccessHandler();
        sut.setClient(client);

        // when
        sut.handle(mock(EzyEvent.class));

        // then
        Asserts.assertTrue(captureEnableCompression(client));
    }

    @Test
    public void handleWhenDisableCompression() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyClientConfig config = EzyClientConfig.builder().build();
        when(client.getConfig()).thenReturn(config);
        EzyConnectionSuccessHandler sut = new EzyConnectionSuccessHandler();
        sut.setClient(client);

        // when
        sut.handle(mock(EzyEvent.class));

        // then
        Asserts.assertFalse(captureEnableCompression(client));
    }

    @Test
    public void handleSendClientTypeAndVersion() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);
        EzyConnectionSuccessHandler sut = new EzyConnectionSuccessHandler();
        sut.setClient(client);

        // when
        sut.handle(mock(EzyEvent.class));

        // then
        EzyArray data = captureHandshakeData(client);
        Asserts.assertEquals(data.get(2, String.class), EzyClientConstants.CLIENT_TYPE);
        Asserts.assertEquals(data.get(3, String.class), EzyClientConstants.CLIENT_VERSION);
    }

    private static boolean captureEnableCompression(EzyClientForTest client) {
        return captureHandshakeData(client).get(7, boolean.class);
    }

    private static EzyArray captureHandshakeData(EzyClientForTest client) {
        EzyRequest request = null;
        for (Invocation invocation : mockingDetails(client).getInvocations()) {
            if (invocation.getMethod().getName().equals("send")
                && invocation.getArguments()[0] instanceof EzyRequest) {
                request = (EzyRequest) invocation.getArguments()[0];
            }
        }
        Asserts.assertEqualsType(request, EzyHandshakeRequest.class);
        return (EzyArray) request.serialize();
    }
}
