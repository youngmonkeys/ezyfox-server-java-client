package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzyPingScheduleAware;

/**
 * Created by tavandung12 on 10/1/18.
 */

public abstract class EzyHandshakeHandler
        extends EzyAbstractDataHandler
        implements EzyPingScheduleAware {

    protected EzyPingSchedule pingSchedule;

    @Override
    public void handle(EzyArray data) {
    	preHandle(data);
        pingSchedule.start();
        handleLogin(data);
        postHandle(data);
    }
    
    protected void preHandle(EzyArray data) {
    	this.client.setSessionId(data.get(2, long.class));
    	this.client.setSessionToken(data.get(1, String.class));
    }

    protected void postHandle(EzyArray data) {}

    protected void handleLogin(EzyArray data) {
        EzyRequest loginRequest = getLoginRequest();
        client.send(loginRequest);
    }

    protected abstract EzyRequest getLoginRequest();

    @Override
    public void setPingSchedule(EzyPingSchedule pingSchedule) {
        this.pingSchedule = pingSchedule;
    }
}
