package com.tvd12.ezyfoxserver.client.entity;

import java.io.Serializable;

import com.tvd12.ezyfox.entity.EzyEntity;
import com.tvd12.ezyfoxserver.socket.EzyPacket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EzySimpleClientUser extends EzyEntity implements EzyClientUser, Serializable {
	private static final long serialVersionUID = -7230916678168758064L;

	protected long id;
	protected String name;
	protected int zoneId;
	protected EzyClientSession session;
	
	@Override
	public void send(EzyPacket packet) {
		session.send(packet);
	}
	
	@Override
	public void sendNow(EzyPacket packet) {
		send(packet);
	}
	
	@Override
	public void destroy() {
		session.destroy();
	}
	
}
