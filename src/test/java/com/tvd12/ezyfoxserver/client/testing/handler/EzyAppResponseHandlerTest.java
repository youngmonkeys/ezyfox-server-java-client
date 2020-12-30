package com.tvd12.ezyfoxserver.client.testing.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppResponseHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;

public class EzyAppResponseHandlerTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void handleAppNotNullDataHandlerNotNull() {
		// given
		int appId = new Random().nextInt();
		String cmd = "testCommand";
		EzyData responseData = EzyEntityArrays.newArray("test");
		EzyArray commandData = EzyEntityArrays.newArray(
				cmd,
				responseData
		);
		EzyArray data = EzyEntityArrays.newArray(
				appId,
				commandData
		);
		EzyApp app = mock(EzyApp.class);
		EzyClientForTest client = mock(EzyClientForTest.class);
		when(client.getAppById(appId)).thenReturn(app);
		EzyAppDataHandler dataHandler = mock(EzyAppDataHandler.class);
		when(app.getDataHandler(cmd)).thenReturn(dataHandler);
		EzyAppResponseHandler sut = new EzyAppResponseHandler();
		sut.setClient(client);
		
		
		// when
		sut.handle(data);
		
		// then
		verify(client, times(1)).getAppById(appId);
		verify(app, times(1)).getDataHandler(cmd);
	}
	
	@Test
	public void handleAppNotNullDataHandlerNull() {
		// given
		int appId = new Random().nextInt();
		String cmd = "testCommand";
		EzyData responseData = EzyEntityArrays.newArray("test");
		EzyArray commandData = EzyEntityArrays.newArray(
				cmd,
				responseData
		);
		EzyArray data = EzyEntityArrays.newArray(
				appId,
				commandData
		);
		EzyApp app = mock(EzyApp.class);
		EzyClientForTest client = mock(EzyClientForTest.class);
		when(client.getAppById(appId)).thenReturn(app);
		when(app.getDataHandler(cmd)).thenReturn(null);
		EzyAppResponseHandler sut = new EzyAppResponseHandler();
		sut.setClient(client);
		
		
		// when
		sut.handle(data);
		
		// then
		verify(client, times(1)).getAppById(appId);
		verify(app, times(1)).getDataHandler(cmd);
	}
	
	@Test
	public void handleAppNull() {
		// given
		int appId = new Random().nextInt();
		String cmd = "testCommand";
		EzyData responseData = EzyEntityArrays.newArray("test");
		EzyArray commandData = EzyEntityArrays.newArray(
				cmd,
				responseData
		);
		EzyArray data = EzyEntityArrays.newArray(
				appId,
				commandData
		);
		EzyClientForTest client = mock(EzyClientForTest.class);
		when(client.getAppById(appId)).thenReturn(null);
		EzyAppResponseHandler sut = new EzyAppResponseHandler();
		sut.setClient(client);
		
		
		// when
		sut.handle(data);
		
		// then
		verify(client, times(1)).getAppById(appId);
	}
	
}
