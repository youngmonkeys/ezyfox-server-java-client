package com.tvd12.ezyfoxserver.client.testing.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.constant.EzyStatusCodes;
import com.tvd12.ezyfoxserver.client.handler.EzyUdpHandshakeHandler;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;

public class EzyUdpHandshakeHandlerTest {

	@Test
	public void handleStatusOK() {
		// given
		int responseCode = EzyStatusCodes.OK;
		EzyUTSocketClient socketClient = new EzyUTSocketClient();
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
		EzyUTSocketClient socketClient = new EzyUTSocketClient();
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
