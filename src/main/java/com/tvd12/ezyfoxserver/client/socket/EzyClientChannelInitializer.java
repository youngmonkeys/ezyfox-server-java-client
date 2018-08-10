package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfox.codec.EzyCodecCreator;
import com.tvd12.ezyfoxserver.client.context.EzyClientContext;
import com.tvd12.ezyfoxserver.client.handler.EzyClientHandler;
import com.tvd12.ezyfoxserver.netty.client.codec.EzyCombinedCodec;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;

public abstract class EzyClientChannelInitializer extends ChannelInitializer<Channel> {

	protected EzyClientContext context;
	protected EzyCodecCreator codecCreator;
	
	protected EzyClientChannelInitializer(Builder<?> builder) {
		this.context = builder.context;
		this.codecCreator = builder.codecCreator;
	}
	
	@Override
	protected final void initChannel(Channel ch) throws Exception {
		initChannel0(ch);
		initChannel1(ch);
	}
	
	protected void initChannel0(Channel ch) throws Exception {
	}
	
	private void initChannel1(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		ChannelOutboundHandler encoder = (ChannelOutboundHandler) codecCreator.newEncoder();
		ChannelInboundHandlerAdapter decoder = (ChannelInboundHandlerAdapter) codecCreator.newDecoder(65536);
		pipeline.addLast("codec-1", new EzyCombinedCodec(decoder, encoder));
		pipeline.addLast("handler", createDataHandler());
		pipeline.addLast("codec-2", new EzyCombinedCodec(decoder, encoder));
	}
	
	private EzyClientHandler createDataHandler() {
		EzyClientHandler handler = newDataHandler();
		handler.setContext(context);
		return handler;
	}
	
	protected abstract EzyClientHandler newDataHandler();

	@SuppressWarnings("unchecked")
	public static abstract class Builder<B extends Builder<B>> 
			implements EzyBuilder<EzyClientChannelInitializer> {
		
		protected EzyClientContext context;
		protected EzyCodecCreator codecCreator;
		
		public B context(EzyClientContext context) {
			this.context = context;
			return (B) this;
		}
		
		public B codecCreator(EzyCodecCreator codecCreator) {
			this.codecCreator = codecCreator;
			return (B) this;
		}
	}
 }