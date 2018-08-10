package com.tvd12.ezyfoxserver.client.setting;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EzySimpleSocketSetting implements EzySocketSetting {
	
	protected int serverPort;
	protected String serverHost;
	protected String codecCreator;
	
}
