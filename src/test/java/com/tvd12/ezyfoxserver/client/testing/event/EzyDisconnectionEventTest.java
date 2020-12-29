package com.tvd12.ezyfoxserver.client.testing.event;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.event.EzyDisconnectionEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;

public class EzyDisconnectionEventTest {

	@Test
	public void test() {
		// given
		int reason = new Random().nextInt();
		EzyDisconnectionEvent event = new EzyDisconnectionEvent(reason);
		
		// when
		// then
		assert event.getReason() == reason;
		assert event.getType() == EzyEventType.DISCONNECTION;
	}
	
}
