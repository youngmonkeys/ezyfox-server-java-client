package com.tvd12.ezyfoxserver.client.testing.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.entity.EzySimpleApp;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.handler.EzyAppAccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;

public class EzyAppAccessHandlerTest {

	@Test
	public void handle() {
		// given
		int appId = new Random().nextInt();
		String appName = "testAppName";
		EzyZone zone = mock(EzyZone.class);
		
		EzyClientForTest client = mock(EzyClientForTest.class);
		when(client.getZone()).thenReturn(zone);
		
		EzyAppManager appManager = mock(EzyAppManager.class);
		when(zone.getAppManager()).thenReturn(appManager);
		when(zone.getClient()).thenReturn(client);
		
		EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
		when(client.getHandlerManager()).thenReturn(handlerManager);
		
		EzyAppDataHandlers appDataHandlers = new EzyAppDataHandlers();
		when(handlerManager.getAppDataHandlers(appName)).thenReturn(appDataHandlers);

		EzyArray data = EzyEntityArrays.newArray(
				appId,
				appName
		);
		EzyAppAccessHandler sut = new EzyAppAccessHandler();
		sut.setClient(client);
		
		// when
		sut.handle(data);
		
		// then
		EzyApp app = new EzySimpleApp(zone, appId, appName);
		
		verify(client, times(1)).getZone();
		verify(zone, times(1)).getAppManager();
		verify(appManager, times(1)).addApp(app);
	}
	
}
