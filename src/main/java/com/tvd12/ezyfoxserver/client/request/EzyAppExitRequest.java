package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

public class EzyAppExitRequest implements EzyRequest {
    private static final long serialVersionUID = -8154334504343982160L;

    protected final int appId;

    public EzyAppExitRequest(int appId) {
        this.appId = appId;
    }

    @Override
    public EzyData serialize() {
        return EzyEntityFactory.newArrayBuilder()
            .append(appId)
            .build();
    }

    @Override
    public Object getCommand() {
        return EzyCommand.APP_EXIT;
    }
}
