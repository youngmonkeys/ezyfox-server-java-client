package com.tvd12.ezyfoxserver.client.context;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.entity.EzyClientUser;
import com.tvd12.ezyfoxserver.context.EzyContext;

public interface EzyClientContext 
		extends EzyPluginRequester, EzyContext, EzyDestroyable {

	EzyClient getClient();
	
	EzyClientUser getMe();
	
	void addAppContext(EzyClientAppContext context);
	
	EzyClientAppContext getAppContext(int appId);
	
	EzyClientAppContext getAppContext(String appName);
	
}
