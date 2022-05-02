package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;

public interface EzyAppDataHandler<D extends EzyData> {

    void handle(EzyApp app, D data);
}
