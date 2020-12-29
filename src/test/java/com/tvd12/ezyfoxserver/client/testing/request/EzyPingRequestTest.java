package com.tvd12.ezyfoxserver.client.testing.request;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyPingRequest;

public class EzyPingRequestTest {

	@Test
	public void serialize() {
		// given
		EzyPingRequest request = EzyPingRequest.getInstance();
		
		// when
		EzyData actual = request.serialize();
		
		// then
		assert actual == EzyEntityFactory.EMPTY_ARRAY;
		assert request.getCommand() == EzyCommand.PING;
		
	}
	
}
