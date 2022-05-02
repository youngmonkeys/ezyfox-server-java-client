package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyStatusCodes;
import com.tvd12.ezyfoxserver.client.handler.EzyUdpHandshakeHandler;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyUdpHandshakeHandlerTest {

    @Test
    public void handleStatusOK() {
        // given
        int responseCode = EzyStatusCodes.OK;
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        EzyArray data = EzyEntityArrays.newArray(
            responseCode
        );
        EzyClient client = mock(EzyUTClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        EzyUdpHandshakeHandler sut = new EzyUdpHandshakeHandler();
        sut.setClient(client);


        // when
        sut.handle(data);

        // then
        verify(client, times(1)).getSocket();
    }

    @Test
    public void handleStatusNotOK() {
        // given
        int responseCode = -1;
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyUTSocketClient socketClient = new EzyUTSocketClient(config);
        EzyArray data = EzyEntityArrays.newArray(
            responseCode
        );
        EzyClient client = mock(EzyUTClient.class);
        when(client.getSocket()).thenReturn(socketClient);
        EzyUdpHandshakeHandler sut = new EzyUdpHandshakeHandler();
        sut.setClient(client);


        // when
        sut.handle(data);

        // then
        verify(client, times(1)).getSocket();
    }
}
