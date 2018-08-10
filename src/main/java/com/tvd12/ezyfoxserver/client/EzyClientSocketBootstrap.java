package com.tvd12.ezyfoxserver.client;

import com.tvd12.ezyfoxserver.client.socket.EzyClientChannelInitializer;
import com.tvd12.ezyfoxserver.client.socket.EzyClientSocketChannelInitializer;

public class EzyClientSocketBootstrap extends EzyClientBootstrap {

	private String host;
	private int port;
	
	protected EzyClientSocketBootstrap(Builder builder) {
		super(builder);
		this.host = builder.host;
		this.port = builder.port;
	}
	
	@Override
	protected String getHost() {
		return host;
	}
	
	@Override
	protected int getPort() {
		return port;
	}
	
	@Override
	protected EzyClientChannelInitializer.Builder<?> newChannelInitializerBuilder() {
		return EzyClientSocketChannelInitializer.builder();
	}
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends EzyClientBootstrap.Builder<Builder> {
		
		private String host;
		private int port;
		
		public Builder host(String host) {
			this.host = host;
			return this;
		}
		
		public Builder port(int port) {
			this.port = port;
			return this;
		}
		
		@Override
		public EzyClientBootstrap build() {
			return new EzyClientSocketBootstrap(this);
		}
		
	}
	
}
