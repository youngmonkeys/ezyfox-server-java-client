package com.tvd12.ezyfoxserver.client.setting;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EzySimpleSocketSetting implements EzySocketSetting {
	
	protected int serverPort;
	protected String serverHost;
	protected String codecCreator;
	
	public EzySimpleSocketSetting() {
		this.serverPort = 3005;
		this.serverHost = "127.0.0.1";
		this.codecCreator = "com.tvd12.ezyfoxserver.netty.codec.MsgPackCodecCreator";
	}
	
	
}
