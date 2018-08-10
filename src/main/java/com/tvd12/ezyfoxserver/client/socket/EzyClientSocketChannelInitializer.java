package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfoxserver.client.handler.EzyClientHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyClientSocketHandler;

public class EzyClientSocketChannelInitializer extends EzyClientChannelInitializer {

	protected EzyClientSocketChannelInitializer(Builder builder) {
		super(builder);
	}
	
	@Override
	protected EzyClientHandler newDataHandler() {
		return new EzyClientSocketHandler();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends EzyClientChannelInitializer.Builder<Builder> {
		
		@Override
		public EzyClientSocketChannelInitializer build() {
			return new EzyClientSocketChannelInitializer(this);
		}
	    
	}
 }