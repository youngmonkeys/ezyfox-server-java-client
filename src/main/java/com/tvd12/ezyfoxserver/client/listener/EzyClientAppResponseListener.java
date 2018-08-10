package com.tvd12.ezyfoxserver.client.listener;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.context.EzyClientAppContext;

public interface EzyClientAppResponseListener<P extends EzyData> {

	void execute(EzyClientAppContext ctx, P params);
	
}
