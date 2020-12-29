package com.tvd12.ezyfoxserver.client.testing.event;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.event.EzyConnectionSuccessEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;

public class EzyConnectionSuccessEventTest {

	@Test
	public void test() {
		// given
		EzyConnectionSuccessEvent event = new EzyConnectionSuccessEvent();
		
		// when
		// then
		assert event.getType() == EzyEventType.CONNECTION_SUCCESS;
	}
	
}
