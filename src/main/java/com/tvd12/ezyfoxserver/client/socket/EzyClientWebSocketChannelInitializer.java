package com.tvd12.ezyfoxserver.client.socket;

import java.net.URI;

import com.tvd12.ezyfoxserver.client.handler.EzyClientWebSocketHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;

public class EzyClientWebSocketChannelInitializer extends EzyClientChannelInitializer {

	protected URI uri;
	
	protected EzyClientWebSocketChannelInitializer(Builder builder) {
		super(builder);
		this.uri = builder.uri;
	}
	
	@Override
	protected void initChannel0(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("http-client-codec", new HttpClientCodec());
		pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(64 * 1024));
		pipeline.addLast("chunked-write-handler", new ChunkedWriteHandler());
		pipeline.addLast("ws-client-protocol-handler", newWebSocketClientProtocolHandler());
	}
	
	@Override
	protected EzyClientWebSocketHandler newDataHandler() {
		return new EzyClientWebSocketHandler();
	}
	
	protected WebSocketClientProtocolHandler newWebSocketClientProtocolHandler() {
		WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
				uri,
				WebSocketVersion.V13,
				null,
				false,
				new DefaultHttpHeaders()
		);
		return new WebSocketClientProtocolHandler(handshaker);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends EzyClientChannelInitializer.Builder<Builder> {
		
		protected URI uri;
		
		public Builder uri(URI uri) {
			this.uri = uri;
			return this;
		}
		
		@Override
		public EzyClientWebSocketChannelInitializer build() {
			return new EzyClientWebSocketChannelInitializer(this);
		}
	    
	}
 }