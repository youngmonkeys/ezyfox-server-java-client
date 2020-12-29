package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

/**
 * Created by tavandung12 on 10/2/18.
 */

public class EzyPingRequest implements EzyRequest {
	private static final long serialVersionUID = 2780983056457516371L;
	
	private static final EzyPingRequest INSTANCE = new EzyPingRequest();
	
	private EzyPingRequest() {}
	
	public static EzyPingRequest getInstance() {
		return INSTANCE;
	}
	
	@Override
    public EzyData serialize() {
        return EzyEntityFactory.EMPTY_ARRAY;
    }

    @Override
    public Object getCommand() {
        return EzyCommand.PING;
    }
}
