package com.tvd12.ezyfoxserver.client.testing.codec;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.codec.MsgPackByteToObjectDecoder;
import com.tvd12.ezyfox.codec.MsgPackObjectToByteEncoder;
import com.tvd12.ezyfoxserver.client.codec.EzySimpleCodecFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionType;

public class EzySimpleCodecFactoryTest {

	@Test
	public void newEncoder() {
		// given
		EzySimpleCodecFactory factory = new EzySimpleCodecFactory();
		
		// when
		Object encoder = factory.newEncoder(EzyConnectionType.SOCKET);
		
		// then
		assert encoder.getClass() == MsgPackObjectToByteEncoder.class;
	}
	
	@Test
	public void newDecoder() {
		// given
		EzySimpleCodecFactory factory = new EzySimpleCodecFactory();
		
		// when
		Object decoder = factory.newDecoder(EzyConnectionType.SOCKET);
		
		// then
		assert decoder.getClass() == MsgPackByteToObjectDecoder.class;
	}
	
}
