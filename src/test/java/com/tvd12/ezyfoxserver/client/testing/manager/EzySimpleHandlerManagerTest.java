package com.tvd12.ezyfoxserver.client.testing.manager;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyAppAccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyAppExitHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppResponseHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionFailureHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyDisconnectionHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginErrorHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginSuccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyPongHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyUdpHandshakeHandler;
import com.tvd12.ezyfoxserver.client.manager.EzySimpleHandlerManager;

import static org.mockito.Mockito.*;

public class EzySimpleHandlerManagerTest {

	@Test
	public void getDataHandler() {
		// given
		EzyClient client = mock(EzyClient.class);
		EzySimpleHandlerManager sut = new EzySimpleHandlerManager(client);
		String cmd = "testCommand";
		EzyDataHandler dataHandler = mock(EzyDataHandler.class);
		sut.addDataHandler(cmd, dataHandler);
		
		// when
		EzyDataHandler actualDataHandler = sut.getDataHandler(cmd);
		
		// then
		assert actualDataHandler == dataHandler;
		assert sut.getDataHandlers() != null;
		assert sut.getDataHandler(EzyCommand.PONG).getClass() == EzyPongHandler.class;
		assert sut.getDataHandler(EzyCommand.LOGIN).getClass() == EzyLoginSuccessHandler.class;
		assert sut.getDataHandler(EzyCommand.LOGIN_ERROR).getClass() == EzyLoginErrorHandler.class;
		assert sut.getDataHandler(EzyCommand.APP_ACCESS).getClass() == EzyAppAccessHandler.class;
		assert sut.getDataHandler(EzyCommand.APP_REQUEST).getClass() == EzyAppResponseHandler.class;
		assert sut.getDataHandler(EzyCommand.APP_EXIT).getClass() == EzyAppExitHandler.class;
		assert sut.getDataHandler(EzyCommand.UDP_HANDSHAKE).getClass() == EzyUdpHandshakeHandler.class;
	}
	
	@Test
	public void getEventHandler() {
		// given
		EzyClient client = mock(EzyClient.class);
		EzySimpleHandlerManager sut = new EzySimpleHandlerManager(client);
		EzyConstant eventType = EzyEventType.CONNECTION_SUCCESS;
		EzyEventHandler<?> eventHandler = mock(EzyEventHandler.class);
		sut.addEventHandler(eventType, eventHandler);
		
		// when
		EzyEventHandler<?> actualEventHandler = sut.getEventHandler(eventType);
		
		// then
		assert actualEventHandler == eventHandler;
		assert sut.getEventHandlers() != null;
		assert sut.getEventHandler(EzyEventType.CONNECTION_FAILURE).getClass() == EzyConnectionFailureHandler.class;
		assert sut.getEventHandler(EzyEventType.DISCONNECTION).getClass() == EzyDisconnectionHandler.class;
	}
	
	@Test
	public void getAppDataHandlers() {
		// given
		EzyClient client = mock(EzyClient.class);
		EzySimpleHandlerManager sut = new EzySimpleHandlerManager(client);
		
		// when
		String appName = "testAppName";
		EzyAppDataHandlers handlers1 = sut.getAppDataHandlers(appName);
		EzyAppDataHandlers handlers2 = sut.getAppDataHandlers(appName);
		
		// then
		assert handlers1 == handlers2;
	}
	
	@Test
	public void getPluginDataHandlers() {
		// given
		EzyClient client = mock(EzyClient.class);
		EzySimpleHandlerManager sut = new EzySimpleHandlerManager(client);
		
		// when
		String pluginName = "pluginName";
		EzyPluginDataHandlers handlers1 = sut.getPluginDataHandlers(pluginName);
		EzyPluginDataHandlers handlers2 = sut.getPluginDataHandlers(pluginName);
		
		// then
		assert handlers1 == handlers2;
	}
	
}
