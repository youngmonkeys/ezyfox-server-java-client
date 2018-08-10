/**
 * 
 */
package com.tvd12.ezyfoxserver.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

/**
 * @author tavandung12
 *
 */
public class EzyClientWebSocketHandler extends EzyClientSocketHandler {

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		getLogger().debug("event trigged {}", evt);
		if(evt.equals(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
			connectionActive(ctx);
		}
	}

}
