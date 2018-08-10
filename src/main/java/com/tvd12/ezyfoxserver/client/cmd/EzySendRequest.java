package com.tvd12.ezyfoxserver.client.cmd;

import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.command.EzyCommand;
import com.tvd12.ezyfoxserver.entity.EzyDeliver;

public interface EzySendRequest extends EzyCommand<Boolean> {

	EzySendRequest sender(EzyDeliver sender);
	
	EzySendRequest request(EzyRequest request);
	
}
