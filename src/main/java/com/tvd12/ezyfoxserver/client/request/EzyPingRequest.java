package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.util.EzyEntityBuilders;
import com.tvd12.ezyfoxserver.client.constants.EzyClientCommand;

import lombok.Builder;

@Builder
public class EzyPingRequest extends EzyEntityBuilders implements EzyRequest {

	@Override
	public Object getData() {
		return newArrayBuilder().build();
	}

	@Override
	public EzyConstant getCommand() {
		return EzyClientCommand.PING;
	}

	
	
	
	
}
