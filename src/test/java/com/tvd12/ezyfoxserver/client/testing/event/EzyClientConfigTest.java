package com.tvd12.ezyfoxserver.client.testing.event;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;

public class EzyClientConfigTest {

	@Test
	public void test() {
		// given
		String zoneName = "testZone";
		String clientName = "testClient";
		int maxReconnectCount = new Random().nextInt();
		int reconnectPeriod = new Random().nextInt();
		
		EzyClientConfig config = EzyClientConfig.builder()
				.zoneName(zoneName)
				.clientName(clientName)
				.reconnectConfigBuilder()
					.enable(true)
					.maxReconnectCount(maxReconnectCount)
					.reconnectPeriod(reconnectPeriod)
					.done()
				.build();
		
		// when
		// then
		assert config.getClientName().equals(clientName);
		assert config.getZoneName().equals(zoneName);
		assert config.getReconnect().isEnable();
		assert config.getReconnect().getMaxReconnectCount() == maxReconnectCount;
		assert config.getReconnect().getReconnectPeriod() == reconnectPeriod;
	}
	
	@Test
	public void testClientNameNull() {
		// given
		String zoneName = "testZone";
		
		EzyClientConfig config = EzyClientConfig.builder()
				.zoneName(zoneName)
				.build();
		
		// when
		// then
		assert config.getClientName().equals(zoneName);
	}
	
}
