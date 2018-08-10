package com.tvd12.ezyfoxserver.client.wrapper;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfoxserver.client.controller.EzyClientAppController;

public interface EzyClientAppControllers extends EzyDestroyable {
	
	@SuppressWarnings("rawtypes")
	EzyClientAppController getController(EzyConstant cmd);
	
	@SuppressWarnings("rawtypes")
	void addController(EzyConstant cmd, EzyClientAppController ctrl);
	
}
