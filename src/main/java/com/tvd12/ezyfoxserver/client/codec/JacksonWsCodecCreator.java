package com.tvd12.ezyfoxserver.client.codec;

import io.netty.channel.ChannelOutboundHandler;

public class JacksonWsCodecCreator extends JacksonCodecCreator {

	public JacksonWsCodecCreator() {
		super();
	}
	
	@Override
	public ChannelOutboundHandler newEncoder() {
		return new JacksonMessageToTextWsFrameEncoder(serializer);
	}

}
