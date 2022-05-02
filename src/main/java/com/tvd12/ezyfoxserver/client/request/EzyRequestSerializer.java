package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

public interface EzyRequestSerializer {

    EzyArray serialize(EzyCommand cmd, EzyArray data);
}

