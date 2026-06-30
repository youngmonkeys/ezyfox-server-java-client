package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EzyPluginResponseHandler extends EzyAbstractDataHandler {

    @Override
    public void handle(EzyArray data) {
        int pluginId = data.get(0, int.class);
        EzyArray commandData = data.get(1, EzyArray.class);
        String cmd = commandData.get(0, String.class);
        EzyData responseData = commandData.get(1, EzyData.class, null);

        EzyPlugin plugin = client.getPluginById(pluginId);
        if (plugin == null) {
            logger.info("receive message when has not joined plugin yet");
            return;
        }
        EzyPluginDataHandler dataHandler = plugin.getDataHandler(cmd);
        if (dataHandler != null) {
            dataHandler.handle(plugin, responseData);
        } else {
            logger.info("plugin: {} has no handler for command: {}", plugin.getName(), cmd);
        }
    }
}

