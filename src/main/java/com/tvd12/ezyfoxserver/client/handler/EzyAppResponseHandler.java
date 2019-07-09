package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;

/**
 * Created by tavandung12 on 9/30/18.
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EzyAppResponseHandler extends EzyAbstractDataHandler {

	@Override
    public void handle(EzyArray data) {
        int appId = data.get(0, int.class);
        EzyArray commandData = data.get(1, EzyArray.class);
        String cmd = commandData.get(0, String.class);
        EzyData responseData = commandData.get(1, EzyData.class, null);

        EzyApp app = client.getAppById(appId);
        EzyAppDataHandler dataHandler = app.getDataHandler(cmd);
        if(dataHandler != null)
            dataHandler.handle(app, responseData);
        else
            logger.warn("app: {} has no handler for command: {}", app.getName(), cmd);
    }
}
