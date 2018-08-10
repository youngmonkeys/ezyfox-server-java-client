package com.tvd12.ezyfoxserver.client.context;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfoxserver.client.entity.EzyClientUser;
import com.tvd12.ezyfoxserver.context.EzyContext;

public interface EzyClientAppContext 
		extends EzyPluginRequester, EzyContext, EzyDestroyable {

	int getAppId();
	
	String getAppName();
	
	EzyClientUser getMe();
	
	EzyClientContext getParent();
	
	<T> T get(Class<T> clazz);
	
	void sendRequest(Object requestId, EzyData params);
	
}
