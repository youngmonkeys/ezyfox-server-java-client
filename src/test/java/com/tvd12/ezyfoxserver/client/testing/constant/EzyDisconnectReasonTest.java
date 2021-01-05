package com.tvd12.ezyfoxserver.client.testing.constant;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;

public class EzyDisconnectReasonTest {

	@Test
	public void getName() {
		// given
		EzyDisconnectReason sut = EzyDisconnectReason.IDLE;
		
		// when
		// then
		sut.getName().equals(sut.toString());
	}
	
	@Test
	public void valueOf() {
		// given
		// when
		// then
		EzyDisconnectReason sut = 
				EzyDisconnectReason.valueOf(EzyDisconnectReason.CLOSE.getId());
		assert sut != null;
	}
	
	@Test
	public void valueOfReturnNull() {
		// given
		// when
		// then
		EzyDisconnectReason sut = 
				EzyDisconnectReason.valueOf(-1000);
		assert sut == null;
	}
	
}
