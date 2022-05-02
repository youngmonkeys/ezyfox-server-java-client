package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClientAware;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;

public abstract class EzyAbstractEventHandler<E extends EzyEvent>
    extends EzyLoggable
    implements EzyEventHandler<E>, EzyClientAware {

    protected EzyClient client;

    @Override
    public void setClient(EzyClient client) {
        this.client = client;
    }
}
