package com.tvd12.ezyfoxserver.client.controller;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.context.EzyClientContext;
import com.tvd12.ezyfoxserver.client.entity.EzyClientSession;

public class EzyLoginErrorController 
		extends EzyAbstractController 
		implements EzyClientController<EzyClientSession> {

	@Override
	public void handle(EzyClientContext ctx, EzyClientSession rev, EzyArray data) {
		getLogger().info("login error {}", data);
	}

}
