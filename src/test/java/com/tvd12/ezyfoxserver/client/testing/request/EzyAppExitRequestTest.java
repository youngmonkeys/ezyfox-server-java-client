package com.tvd12.ezyfoxserver.client.testing.request;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyAppExitRequest;

public class EzyAppExitRequestTest {

	@Test
	public void serialize() {
		// given
		int appId = new Random().nextInt();
		EzyAppExitRequest request = new EzyAppExitRequest(appId);
		
		// when
		EzyData actual = request.serialize();
		
		// then
		EzyData expected = EzyEntityArrays.newArray(appId);
		assert actual.equals(expected);
		assert request.getCommand() == EzyCommand.APP_EXIT;
	}
	
}
