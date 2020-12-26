package com.tvd12.ezyfoxserver.client.constant;

public enum EzyDisconnectReason implements EzyConstant {

	CLOSE(-1),
	UNKNOWN(0),
	IDLE(1),
	NOT_LOGGED_IN(2),
	ANOTHER_SESSION_LOGIN(3),
	ADMIN_BAN(4),
    ADMIN_KICK(5),
    MAX_REQUEST_PER_SECOND(6),
    MAX_REQUEST_SIZE(7),
    SERVER_ERROR(8),
	SERVER_NOT_RESPONDING(400);

	private final int id;
	
	private EzyDisconnectReason(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return toString();
	}
	
	public static EzyDisconnectReason valueOf(int id) {
		for(EzyDisconnectReason reason : values())
			if(reason.getId() == id)
				return reason;
		return null;
	}
	
}
