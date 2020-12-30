package com.tvd12.ezyfoxserver.client.testing.handler;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.entity.EzySimpleUser;
import com.tvd12.ezyfoxserver.client.entity.EzySimpleZone;
import com.tvd12.ezyfoxserver.client.entity.EzyUser;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginSuccessHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;

import static org.mockito.Mockito.*;

public class EzyLoginSuccessHandlerTest {

	@Test
	public void handleTest() {
		// given
		int zoneId = new Random().nextInt();
		String zoneName = "testZoneName";
		long userId = new Random().nextLong();
		String username = "testUsername";
		EzyData responseData = EzyEntityArrays.newArray("test");
		EzyArray data = EzyEntityArrays.newArray(
				zoneId,
				zoneName,
				userId,
				username,
				responseData
		);
		EzyClientForTest client = mock(EzyClientForTest.class);
		EzyZone zone = new EzySimpleZone(client, zoneId, zoneName);
		EzyUser user = new EzySimpleUser(userId, username);
		doNothing().when(client).setMe(user);
		doNothing().when(client).setZone(zone);
		EzyLoginSuccessHandler sut = new EzyLoginSuccessHandler();
		sut.setClient(client);
		
		
		// when
		sut.handle(data);
		
		// then
		verify(client, times(1)).setMe(user);
		verify(client, times(1)).setZone(zone);
	}
	
}
