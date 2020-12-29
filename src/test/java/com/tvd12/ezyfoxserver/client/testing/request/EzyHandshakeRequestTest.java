package com.tvd12.ezyfoxserver.client.testing.request;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyHandshakeRequest;

public class EzyHandshakeRequestTest {

	@Test
	public void serializeTest() {
		// given
		String clientId = "testClientId";
		String clientKey = "testClientKey";
		String clientType = "testClientType";
		String clientVersion = "testClientVersion";
		boolean enableEncryption = true;
		String token = "testToken";
		EzyHandshakeRequest request = new EzyHandshakeRequest(
				clientId, 
				clientKey, 
				clientType, 
				clientVersion, 
				enableEncryption, 
				token
		);
		
		// when
		EzyData actual = request.serialize();
		
		// then
		EzyData expected = EzyEntityArrays.newArray(
				clientId,
				clientKey,
				clientType,
				clientVersion,
				enableEncryption,
				token
		);
		assert actual.equals(expected);
		assert request.getCommand() == EzyCommand.HANDSHAKE;
	}
	
}
