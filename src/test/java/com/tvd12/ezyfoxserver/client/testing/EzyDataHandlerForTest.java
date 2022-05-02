package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfoxserver.client.EzyClientAware;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandler;
import com.tvd12.ezyfoxserver.client.socket.EzyPingScheduleAware;

public interface EzyDataHandlerForTest extends
    EzyDataHandler,
    EzyClientAware,
    EzyPingScheduleAware {}
