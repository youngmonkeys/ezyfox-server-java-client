package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyCloseable;

public interface EzyISocketClient extends EzyCloseable {

	void connectTo(String host, int port);
	
	boolean reconnect();
	
	void disconnect(int reason);
	
	void sendMessage(EzyArray message);
	
	void close();
	
}
