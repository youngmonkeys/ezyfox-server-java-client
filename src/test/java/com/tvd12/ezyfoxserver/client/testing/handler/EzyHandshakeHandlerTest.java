package com.tvd12.ezyfoxserver.client.testing.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.handler.EzyHandshakeHandler;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;

public class EzyHandshakeHandlerTest {

	@Test
	public void handle() {
		// given
		String sessionToken = "testSessionToken";
		long sessionId = new Random().nextLong();
		EzyUTSocketClient socketClient = new EzyUTSocketClient();
		EzyArray data = EzyEntityArrays.newArray(
				"",
				sessionToken,
				sessionId
				
		);
		EzyClient client = mock(EzyClient.class);
		when(client.getSocket()).thenReturn(socketClient);
		EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
		EzyRequest loginRequest = mock(EzyRequest.class);
		EzyHandshakeHandler sut = new EzyHandshakeHandler() {
			protected EzyRequest getLoginRequest() {
				return loginRequest;
			};
		};
		sut.setClient(client);
		sut.setPingSchedule(pingSchedule);
		
		// when
		sut.handle(data);
		
		// then
		verify(client, times(1)).send(loginRequest);
	}
	
}
