package com.tvd12.ezyfoxserver.client.testing.entity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.entity.EzySimpleApp;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;

public class EzySimpleAppTest {

	@Test
	public void sendRequest() {
		// given
		int appId = new Random().nextInt();
		String appName = "testAppName";
		EzyZone zone = mock(EzyZone.class);
		
		EzyClient client = mock(EzyClient.class);
		when(zone.getClient()).thenReturn(client);
		
		EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
		when(client.getHandlerManager()).thenReturn(handlerManager);
		
		EzyAppDataHandlers dataHandlers = new EzyAppDataHandlers();
		when(handlerManager.getAppDataHandlers(appName)).thenReturn(dataHandlers);
		
		EzyRequest request = mock(EzyRequest.class);
		String cmd = "testCommand";
		when(request.getCommand()).thenReturn(cmd);
		EzyData requestData = EzyEntityArrays.newArray("test");
		when(request.serialize()).thenReturn(requestData);
		
		EzySimpleApp app = new EzySimpleApp(zone, appId, appName);

		// when
		app.send(request);
		
		// then
		verify(handlerManager, times(1)).getAppDataHandlers(appName);
		
		verify(client, times(1)).getHandlerManager();
		
		EzyArray commandData = EzyEntityFactory.newArray();
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(appId, commandData);
		verify(client, times(1)).send(EzyCommand.APP_REQUEST, finalRequestData);
	}
	
	@Test
	public void sendEmptyRequestData() {
		// given
		int appId = new Random().nextInt();
		String appName = "testAppName";
		EzyZone zone = mock(EzyZone.class);
		
		EzyClient client = mock(EzyClient.class);
		when(zone.getClient()).thenReturn(client);
		
		EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
		when(client.getHandlerManager()).thenReturn(handlerManager);
		
		EzyAppDataHandlers dataHandlers = new EzyAppDataHandlers();
		when(handlerManager.getAppDataHandlers(appName)).thenReturn(dataHandlers);
		
		String cmd = "testCommand";
		
		EzySimpleApp app = new EzySimpleApp(zone, appId, appName);

		// when
		app.send(cmd);
		
		// then
		verify(handlerManager, times(1)).getAppDataHandlers(appName);
		
		verify(client, times(1)).getHandlerManager();
		
		EzyArray commandData = EzyEntityFactory.newArray();
		EzyData requestData = EzyEntityFactory.EMPTY_OBJECT;
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(appId, commandData);
		verify(client, times(1)).send(EzyCommand.APP_REQUEST, finalRequestData);
	}
	
	@Test
	public void udpSendRequest() {
		// given
		int appId = new Random().nextInt();
		String appName = "testAppName";
		EzyZone zone = mock(EzyZone.class);
		
		EzyClient client = mock(EzyClient.class);
		when(zone.getClient()).thenReturn(client);
		
		EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
		when(client.getHandlerManager()).thenReturn(handlerManager);
		
		EzyAppDataHandlers dataHandlers = new EzyAppDataHandlers();
		when(handlerManager.getAppDataHandlers(appName)).thenReturn(dataHandlers);
		
		EzyRequest request = mock(EzyRequest.class);
		String cmd = "testCommand";
		when(request.getCommand()).thenReturn(cmd);
		EzyData requestData = EzyEntityArrays.newArray("test");
		when(request.serialize()).thenReturn(requestData);
		
		EzySimpleApp app = new EzySimpleApp(zone, appId, appName);

		// when
		app.udpSend(request);
		
		// then
		verify(handlerManager, times(1)).getAppDataHandlers(appName);
		
		verify(client, times(1)).getHandlerManager();
		
		EzyArray commandData = EzyEntityFactory.newArray();
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(appId, commandData);
		verify(client, times(1)).udpSend(EzyCommand.APP_REQUEST, finalRequestData);
	}
	
	@Test
	public void udpSendEmptyRequestData() {
		// given
		int appId = new Random().nextInt();
		String appName = "testAppName";
		EzyZone zone = mock(EzyZone.class);
		
		EzyClient client = mock(EzyClient.class);
		when(zone.getClient()).thenReturn(client);
		
		EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
		when(client.getHandlerManager()).thenReturn(handlerManager);
		
		EzyAppDataHandlers dataHandlers = new EzyAppDataHandlers();
		when(handlerManager.getAppDataHandlers(appName)).thenReturn(dataHandlers);
		
		String cmd = "testCommand";
		
		EzySimpleApp app = new EzySimpleApp(zone, appId, appName);

		// when
		app.udpSend(cmd);
		
		// then
		verify(handlerManager, times(1)).getAppDataHandlers(appName);
		
		verify(client, times(1)).getHandlerManager();
		
		EzyArray commandData = EzyEntityFactory.newArray();
		EzyData requestData = EzyEntityFactory.EMPTY_OBJECT;
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(appId, commandData);
		verify(client, times(1)).udpSend(EzyCommand.APP_REQUEST, finalRequestData);
	}
	
	@Test
	public void propertiesTest() {
		// given
		int appId = new Random().nextInt();
		String appName = "testAppName";
		EzyZone zone = mock(EzyZone.class);
		
		EzyClient client = mock(EzyClient.class);
		when(zone.getClient()).thenReturn(client);
		
		EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
		when(client.getHandlerManager()).thenReturn(handlerManager);
		
		EzyAppDataHandlers dataHandlers = new EzyAppDataHandlers();
		when(handlerManager.getAppDataHandlers(appName)).thenReturn(dataHandlers);

		String cmd = "testCommand";
		EzyAppDataHandler<?> dataHandler = mock(EzyAppDataHandler.class);
		dataHandlers.addHandler(cmd, dataHandler);
		
		EzyRequest request = mock(EzyRequest.class);
		when(request.getCommand()).thenReturn(cmd);
		EzyData requestData = EzyEntityArrays.newArray("test");
		when(request.serialize()).thenReturn(requestData);
		
		EzySimpleApp app = new EzySimpleApp(zone, appId, appName);

		// when
		int actualAppId = app.getId();
		String actualAppName = app.getName();
		EzyClient actualClient = app.getClient();
		EzyZone actualZone = app.getZone();
		EzyAppDataHandler<?> actualDataHandler = app.getDataHandler(cmd);
		
		// then
		assert actualAppId == appId;
		assert actualAppName.equals(appName);
		assert actualClient == client;
		assert actualZone == zone;
		assert actualDataHandler == dataHandler;
		assert app.hashCode() == appId;
		System.out.println(app);
	}
	
}
