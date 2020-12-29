package com.tvd12.ezyfoxserver.client.testing.request;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzySimpleRequestSerializer;

public class EzySimpleRequestSerializerTest {

	@Test
	public void serialize() {
		// given
		EzyCommand cmd = EzyCommand.APP_ACCESS;
		EzyArray data = EzyEntityArrays.newArray("test");
		EzySimpleRequestSerializer serializer = new EzySimpleRequestSerializer();
		
		// when
		EzyData actual = serializer.serialize(cmd, data);
		
		// then
		EzyData expected = EzyEntityArrays.newArray(cmd.getId(), data);
		assert actual.equals(expected);
	}
	
}
