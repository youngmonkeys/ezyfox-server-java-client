package com.tvd12.ezyfoxserver.client.testing.handler;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;

public class EzyConnectionSuccessHandlerTest {

	@Test
	public void handleMustReonnect() {
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
	
}
