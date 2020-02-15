package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;

public interface EzyISocketClient {

	void connectTo(String host, int port);
	
	void sendMessage(EzyArray message);
	
}
