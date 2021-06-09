package com.tvd12.ezyfoxserver.client.config;

public interface EzySocketClientConfig {

	EzySocketClientConfig DEFAULT = new EzySocketClientConfig() {
		@Override
		public boolean isEnableSSL() {
			return false;
		}
	};
	
	boolean isEnableSSL();
	
}
