package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.entity.*;

public class EzyLoginSuccessHandler extends EzyAbstractDataHandler {

    @Override
    public void handle(EzyArray data) {
        EzyData responseData = data.get(4, EzyData.class);
        EzyUser user = newUser(data);
        EzyZone zone = newZone(data);
        ((EzyMeAware) client).setMe(user);
        ((EzyZoneAware) client).setZone(zone);
        handleLoginSuccess(responseData);
        logger.debug("user: {} logged in successfully", user);
    }

    protected void handleLoginSuccess(EzyData responseData) {}

    protected EzyUser newUser(EzyArray data) {
        long userId = data.get(2, long.class);
        String username = data.get(3, String.class);
        return new EzySimpleUser(userId, username);
    }

    protected EzyZone newZone(EzyArray data) {
        int zoneId = data.get(0, int.class);
        String zoneName = data.get(1, String.class);
        return new EzySimpleZone(client, zoneId, zoneName);
    }
}
