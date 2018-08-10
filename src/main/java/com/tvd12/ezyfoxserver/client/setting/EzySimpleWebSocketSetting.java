package com.tvd12.ezyfoxserver.client.setting;

import java.net.URI;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EzySimpleWebSocketSetting implements EzyWebSocketSetting {
	
	protected URI uri;
	protected String codecCreator;
	
}
