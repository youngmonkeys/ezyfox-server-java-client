package com.tvd12.ezyfoxserver.client.constant;

import com.tvd12.ezyfox.constant.EzyConstant;

public enum EzyConnectionType implements EzyConstant {

	SOCKET(1),
	WEBSOCKET(2);

	private final int id;
	
	private EzyConnectionType(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return toString();
	}
	
}
