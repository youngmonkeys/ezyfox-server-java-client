package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

public class EzyPluginInfoRequest implements EzyRequest {
    private static final long serialVersionUID = -8154334512343982160L;

    protected final String pluginName;

    public EzyPluginInfoRequest(String pluginName) {
        this.pluginName = pluginName;
    }

    @Override
    public EzyData serialize() {
        return EzyEntityFactory.newArrayBuilder()
            .append(pluginName)
            .build();
    }

    @Override
    public Object getCommand() {
        return EzyCommand.PLUGIN_INFO;
    }
}
