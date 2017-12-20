package com.tvd12.ezyfoxserver.client.cmd;

import com.tvd12.ezyfoxserver.client.setting.EzySocketSetting;
import com.tvd12.ezyfoxserver.client.setting.EzySocketSettingBuilder;

public interface EzyEnableSocket {

	void execute() throws Exception;
	
	EzyEnableSocket fromMainThread(boolean fromMainThread);
	
	EzyEnableSocket socketSetting(EzySocketSetting socketSetting);
	
	EzyEnableSocket socketSetting(EzySocketSettingBuilder socketSettingBuilder);
	
	
}
