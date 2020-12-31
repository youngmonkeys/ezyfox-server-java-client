package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;

public class EzyLoginErrorHandler extends EzyAbstractDataHandler {

    @Override
    public void handle(EzyArray data) {
        client.disconnect(EzyDisconnectReason.UNAUTHORIZED.getId());
        handleLoginError(data);
    }

    protected void handleLoginError(EzyArray data) {}
}

