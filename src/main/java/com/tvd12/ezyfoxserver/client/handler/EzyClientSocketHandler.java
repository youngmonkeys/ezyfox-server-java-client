package com.tvd12.ezyfoxserver.client.handler;

import io.netty.channel.ChannelHandlerContext;

public class EzyClientSocketHandler extends EzyClientHandler {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.connectionActive(ctx);
	}
	
}
