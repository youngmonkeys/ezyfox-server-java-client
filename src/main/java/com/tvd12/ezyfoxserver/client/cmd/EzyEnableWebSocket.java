package com.tvd12.ezyfoxserver.client.cmd;

import com.tvd12.ezyfoxserver.client.setting.EzyWebSocketSetting;
import com.tvd12.ezyfoxserver.client.setting.EzyWebSocketSettingBuilder;

public interface EzyEnableWebSocket {

	void execute() throws Exception;
	
	EzyEnableWebSocket fromMainThread(boolean fromMainThread);
	
	EzyEnableWebSocket socketSetting(EzyWebSocketSetting socketSetting);
	
	EzyEnableWebSocket socketSetting(EzyWebSocketSettingBuilder socketSettingBuilder);
	
	
}
