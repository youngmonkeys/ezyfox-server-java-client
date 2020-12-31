package com.tvd12.ezyfoxserver.client.testing;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.EzyTcpClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.entity.EzyUser;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;

import static org.mockito.Mockito.*;

import java.util.Random;

public class EzyTcpClientTest {

	@SuppressWarnings("resource")
	@Test
	public void connect() {
		// given
		int reconnectPeriod = 3;
		EzyClientConfig config = EzyClientConfig.builder()
				.reconnectConfigBuilder()
					.maxReconnectCount(0)
					.reconnectPeriod(reconnectPeriod)
					.done()
				.build();
		EzyTcpClient sut = new EzyTcpClient(config);
		
		// when
		sut.connect("unknown", 0);
	}
	
	@SuppressWarnings("resource")
	@Test
	public void connectAgain() {
		// given
		int reconnectPeriod = 3;
		EzyClientConfig config = EzyClientConfig.builder()
				.reconnectConfigBuilder()
					.maxReconnectCount(0)
					.reconnectPeriod(reconnectPeriod)
					.done()
				.build();
		EzyTcpClient sut = new EzyTcpClient(config);
		sut.setStatus(EzyConnectionStatus.CONNECTED);
		
		// when
		sut.connect("unknown", 0);
	}
	
	@SuppressWarnings("resource")
	@Test
	public void reconnectAble() throws Exception {
		// given
		int reconnectPeriod = 3;
		EzyClientConfig config = EzyClientConfig.builder()
				.reconnectConfigBuilder()
					.maxReconnectCount(0)
					.reconnectPeriod(reconnectPeriod)
					.done()
				.build();
		EzyTcpClient sut = new EzyTcpClient(config);
		
		// when
		sut.connect("unknown", 0);
		Thread.sleep(500);
		sut.setStatus(EzyConnectionStatus.FAILURE);
		sut.reconnect();
	}
	
	@SuppressWarnings("resource")
	@Test
	public void reconnectNotAble() {
		// given
		int reconnectPeriod = 3;
		EzyClientConfig config = EzyClientConfig.builder()
				.reconnectConfigBuilder()
					.maxReconnectCount(0)
					.reconnectPeriod(reconnectPeriod)
					.done()
				.build();
		EzyTcpClient sut = new EzyTcpClient(config);
		
		// when
		sut.connect("unknown", 0);
		sut.reconnect();
	}
	
	@SuppressWarnings("resource")
	@Test
	public void disconnect() {
		// given
		int reconnectPeriod = 3;
		EzyClientConfig config = EzyClientConfig.builder()
				.reconnectConfigBuilder()
					.maxReconnectCount(0)
					.reconnectPeriod(reconnectPeriod)
					.done()
				.build();
		EzyTcpClient sut = new EzyTcpClient(config);
		
		// when
		sut.disconnect(EzyDisconnectReason.CLOSE.getId());
	}
	
	@SuppressWarnings("resource")
	@Test
	public void propertiesTest() {
		// given
		String clientName = "testClientName";
		EzyClientConfig config = EzyClientConfig.builder()
				.clientName(clientName)
				.build();
		EzyTcpClient sut = new EzyTcpClient(config);
		
		EzyZone zone = mock(EzyZone.class);
		sut.setZone(zone);
		
		EzyAppManager appManager = mock(EzyAppManager.class);
		when(zone.getAppManager()).thenReturn(appManager);
		
		int appId = new Random().nextInt();
		EzyApp app = mock(EzyApp.class);
		when(appManager.getApp()).thenReturn(app);
		when(appManager.getAppById(appId)).thenReturn(app);
		
		EzyUser me = mock(EzyUser.class);
		sut.setMe(me);
		
		sut.setStatus(EzyConnectionStatus.CONNECTED);
		
		long sessionId = new Random().nextLong();
		sut.setSessionId(sessionId);
		
		String sessionToken = "testSessionToken";
		sut.setSessionToken(sessionToken);
		
		// then
		assert sut.setup() != null;
		assert sut.getName().equals(clientName);
		assert sut.getConfig() == config;
		assert sut.getZone() == zone;
		assert sut.getMe() == me;
		assert sut.getStatus() == EzyConnectionStatus.CONNECTED;
		assert sut.getSocket() != null;
		assert sut.getApp() == app;
		assert sut.getAppById(appId) == app;
	}
	
}
