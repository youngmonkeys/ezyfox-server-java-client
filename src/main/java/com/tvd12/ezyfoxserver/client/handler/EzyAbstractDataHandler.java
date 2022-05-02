package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClientAware;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;

public abstract class EzyAbstractDataHandler
    extends EzyLoggable
    implements EzyDataHandler, EzyClientAware {

    protected EzyClient client;
    protected EzyHandlerManager handlerManager;

    @Override
    public void setClient(EzyClient client) {
        this.client = client;
        this.handlerManager = client.getHandlerManager();
    }
}
