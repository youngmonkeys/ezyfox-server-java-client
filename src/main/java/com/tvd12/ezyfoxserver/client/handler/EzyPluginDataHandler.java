package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;

public interface EzyPluginDataHandler<D extends EzyData> {

    void handle(EzyPlugin app, D data);

}
