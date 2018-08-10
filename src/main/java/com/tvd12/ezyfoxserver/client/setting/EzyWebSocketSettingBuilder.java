package com.tvd12.ezyfoxserver.client.setting;

import java.net.URI;

import com.tvd12.ezyfox.builder.EzyBuilder;

public class EzyWebSocketSettingBuilder implements EzyBuilder<EzyWebSocketSetting> {

	protected URI uri;
	protected String codecCreator;
	
	public EzyWebSocketSettingBuilder() {
		this.codecCreator = "com.tvd12.ezyfox.netty.codec.JacksonCodecCreator";
	}
	
	public static EzyWebSocketSettingBuilder webSocketSettingBuilder() {
		return new EzyWebSocketSettingBuilder();
	}
	
	public EzyWebSocketSettingBuilder uri(URI uri) {
		this.uri = uri;
		return this;
	}
	
	public EzyWebSocketSettingBuilder uri(String uri) {
		return uri(URI.create(uri));
	}
	
	public EzyWebSocketSettingBuilder codecCreator(String codecCreator) {
		this.codecCreator = codecCreator;
		return this;
	}
	
	@Override
	public EzyWebSocketSetting build() {
		EzySimpleWebSocketSetting setting = new EzySimpleWebSocketSetting();
		setting.setUri(uri);
		setting.setCodecCreator(codecCreator);
		return setting;
	}
	
}
