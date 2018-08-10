/**
 * 
 */
package com.tvd12.ezyfoxserver.client;

import java.net.URI;

import com.tvd12.ezyfoxserver.client.socket.EzyClientChannelInitializer;
import com.tvd12.ezyfoxserver.client.socket.EzyClientWebSocketChannelInitializer;

/**
 * @author tavandung12
 *
 */
public class EzyClientWebSocketBootstrap extends EzyClientBootstrap {

    protected URI uri;
    
    protected EzyClientWebSocketBootstrap(Builder builder) {
    		super(builder);
    		this.uri = builder.uri;
    }
    
    @Override
    protected String getHost() {
    		return uri.getHost();
    }
    
    @Override
    protected int getPort() {
    		return uri.getPort();
    }
    
    @Override
    protected EzyClientChannelInitializer.Builder<?> newChannelInitializerBuilder() {
    		return EzyClientWebSocketChannelInitializer.builder().uri(uri);
    }
    
    public static Builder builder() {
    		return new Builder();
    }
    
    public static class Builder extends EzyClientBootstrap.Builder<Builder> {
    	
    		protected URI uri;
        
        public Builder uri(URI uri) {
	        	this.uri = uri;
	        	return this;
        }
        
        @Override
        public EzyClientWebSocketBootstrap build() {
        		return new EzyClientWebSocketBootstrap(this);
        }
    }
    
}
