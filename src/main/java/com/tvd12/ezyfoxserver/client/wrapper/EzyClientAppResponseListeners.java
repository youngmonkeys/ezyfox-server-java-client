package com.tvd12.ezyfoxserver.client.wrapper;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfoxserver.client.listener.EzyClientAppResponseListener;

public interface EzyClientAppResponseListeners extends EzyDestroyable {

	@SuppressWarnings("rawtypes")
	EzyClientAppResponseListener getListener(Object requestId);
	
	@SuppressWarnings("rawtypes")
	void addListener(Object requestId, EzyClientAppResponseListener listener);
	
}
