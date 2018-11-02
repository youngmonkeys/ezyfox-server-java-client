package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;

/**
 * Created by tavandung12 on 10/5/18.
 */

public interface EzyAppDataHandler<D extends EzyData> {

    void handle(EzyApp app, D data);

}
