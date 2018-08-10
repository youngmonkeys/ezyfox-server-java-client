package com.tvd12.ezyfoxserver.client.setting;

import com.tvd12.ezyfox.builder.EzyBuilder;

public class EzySocketSettingBuilder implements EzyBuilder<EzySocketSetting> {

	protected int serverPort;
	protected String serverHost;
	protected String codecCreator;
	
	public EzySocketSettingBuilder() {
		this.serverPort = 3005;
		this.serverHost = "127.0.0.1";
		this.codecCreator = "com.tvd12.ezyfoxserver.netty.client.codec.MsgPackCodecCreator";
	}
	
	public static EzySocketSettingBuilder socketSettingBuilder() {
		return new EzySocketSettingBuilder();
	}
	
	public EzySocketSettingBuilder serverPort(int serverPort) {
		this.serverPort = serverPort;
		return this;
	}
	
	public EzySocketSettingBuilder serverHost(String serverHost) {
		this.serverHost = serverHost;
		return this;
	}
	
	public EzySocketSettingBuilder codecCreator(String codecCreator) {
		this.codecCreator = codecCreator;
		return this;
	}
	
	@Override
	public EzySocketSetting build() {
		EzySimpleSocketSetting setting = new EzySimpleSocketSetting();
		setting.setServerPort(serverPort);
		setting.setServerHost(serverHost);
		setting.setCodecCreator(codecCreator);
		return setting;
	}
	
}
