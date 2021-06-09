package com.tvd12.ezyfoxserver.client.handler;

import java.util.Arrays;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.sercurity.EzyAsyCrypt;
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
    	this.client.setSessionToken(data.get(1, String.class));
    	this.client.setSessionId(data.get(2, long.class));
    	if(client.isEnableSSL()) {
    		this.client.setSessionKey(decrypteSessionKey(data.get(3, byte[].class, null)));
    	}
    }
    
	protected byte[] decrypteSessionKey(byte[] sessionKey) {
		if(sessionKey == null) {
			if(client.isEnableDebug()) {
				return null;
			}
			client.close();
			throw new IllegalStateException(
					"maybe server was not enable SSL, you must enable SSL on server " + 
					"or disable SSL on your client or enable debug mode");
		}
		try {
			return EzyAsyCrypt.builder()
					.privateKey(client.getPrivateKey())
					.build()
					.decrypt(sessionKey);
		} catch (Exception e) {
			throw new IllegalStateException("can not decrypt session key: " + Arrays.toString(sessionKey), e);
		}
	}

    protected void postHandle(EzyArray data) {}

    protected void handleLogin(EzyArray data) {
        EzyRequest loginRequest = getLoginRequest();
        client.send(loginRequest, encryptedLoginRequest());
    }

    protected abstract EzyRequest getLoginRequest();
    
    protected boolean encryptedLoginRequest() {
    	return false;
    }

    @Override
    public void setPingSchedule(EzyPingSchedule pingSchedule) {
        this.pingSchedule = pingSchedule;
    }
}
