package com.tvd12.ezyfoxserver.client.cmd.impl;

import java.util.Set;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.cmd.EzySendRequest;
import com.tvd12.ezyfoxserver.client.context.EzyClientContext;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.serialize.EzyRequestSerializer;
import com.tvd12.ezyfoxserver.command.impl.EzyAbstractCommand;
import com.tvd12.ezyfoxserver.entity.EzyDeliver;
import com.tvd12.ezyfoxserver.socket.EzySimplePacket;

public class EzySendRequestImpl extends EzyAbstractCommand implements EzySendRequest {

	protected EzyDeliver sender;
	protected EzyRequest request;

	protected Set<EzyConstant> unloggableCommands;
	
	protected final EzyRequestSerializer requestSerializer;
	
	public EzySendRequestImpl(EzyClientContext context) {
		this.requestSerializer = context.get(EzyRequestSerializer.class);
		this.unloggableCommands = context.getClient().getUnloggableCommands();
	}
	
	@Override
	public EzySendRequest sender(EzyDeliver sender) {
		this.sender = sender;
		return this;
	}
	
	@Override
	public EzySendRequest request(EzyRequest request) {
		this.request = request;
		return this;
	}
	
	@Override
	public Boolean execute() {
		EzyArray requestParams = serializeToArray(request);
		EzySimplePacket packet = new EzySimplePacket();
		packet.setData(requestParams);
		sender.send(packet);
		debugLogRequest(requestParams);
		return Boolean.TRUE;
	}
	
	protected void debugLogRequest(EzyArray requestParams) {
		if(!unloggableCommands.contains(request.getCommand()))
			getLogger().debug("send to server command: {} with params: {}", request.getCommand(), requestParams);
	}
	
	protected EzyArray serializeToArray(EzyRequest request) {
		return requestSerializer.serializeToArray(request);
	}
	
}
