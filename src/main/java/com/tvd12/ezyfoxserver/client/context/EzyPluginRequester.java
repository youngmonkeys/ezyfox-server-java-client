package com.tvd12.ezyfoxserver.client.context;

import com.tvd12.ezyfox.entity.EzyArray;

public interface EzyPluginRequester {

	void sendPluginRequest(String pluginName, EzyArray data);
	
}
