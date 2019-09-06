package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

public class EzySimpleRequestSerializer implements EzyRequestSerializer {

    @Override
    public EzyArray serialize(EzyCommand cmd, EzyArray data) {
        EzyArray array = EzyEntityFactory.newArrayBuilder()
                .append(cmd.getId())
                .append(data)
                .build();
        return array;
    }
}
