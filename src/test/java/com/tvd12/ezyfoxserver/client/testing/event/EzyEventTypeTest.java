package com.tvd12.ezyfoxserver.client.testing.event;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.event.EzyEventType;

public class EzyEventTypeTest {

	@Test
	public void test() {
		// given
		EzyEventType eventType = EzyEventType.CONNECTION_SUCCESS;
		
		// when
		// then
		assert eventType.getId() == 1;
		assert eventType.getName().equals("CONNECTION_SUCCESS");
	}
	
}
