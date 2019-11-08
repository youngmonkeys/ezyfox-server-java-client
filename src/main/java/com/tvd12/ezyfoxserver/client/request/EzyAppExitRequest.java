package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

/**
 * Created by tavandung12 on 10/3/18.
 */
public class EzyAppExitRequest implements EzyRequest {
	private static final long serialVersionUID = -8154334504343982160L;
	
	protected final int appId;

    public EzyAppExitRequest(int appId) {
    	this.appId = appId;
    }

    @Override
    public EzyData serialize() {
        EzyData answer = EzyEntityFactory.newArrayBuilder()
                .append(appId)
                .build();
        return answer;
    }

    @Override
    public Object getCommand() {
        return EzyCommand.APP_EXIT;
    }
}
