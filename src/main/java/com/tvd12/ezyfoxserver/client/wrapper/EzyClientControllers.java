package com.tvd12.ezyfoxserver.client.wrapper;

import com.tvd12.ezyfox.constant.EzyConstant;

public interface EzyClientControllers {
	
	Object getController(EzyConstant cmd);
	
	void addController(EzyConstant cmd, Object ctrl);
	
}
