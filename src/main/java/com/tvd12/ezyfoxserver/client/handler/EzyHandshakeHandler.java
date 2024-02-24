package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.security.EzyAsyCrypt;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzyPingScheduleAware;

import java.util.Arrays;

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
        if (client.isEnableEncryption()) {
            this.client.setSessionKey(decryptSessionKey(data.get(3, byte[].class, null)));
        }
    }

    protected byte[] decryptSessionKey(byte[] sessionKey) {
        if (sessionKey == null) {
            if (client.isEnableDebug()) {
                return null;
            }
            client.close();
            throw new IllegalStateException(
                "maybe server was not enable SSL, you must enable SSL on server " +
                    "or disable SSL on your client or enable debug mode"
            );
        }
        try {
            return EzyAsyCrypt.builder()
                .privateKey(client.getPrivateKey())
                .build()
                .decrypt(sessionKey);
        } catch (Throwable e) {
            throw new IllegalStateException(
                "can not decrypt session key: " + Arrays.toString(sessionKey),
                e
            );
        }
    }

    protected void postHandle(EzyArray data) {}

    protected void handleLogin(EzyArray data) {
        EzyRequest loginRequest = getLoginRequest();
        client.send(loginRequest, client.isEnableEncryption());
    }

    protected abstract EzyRequest getLoginRequest();

    @Override
    public void setPingSchedule(EzyPingSchedule pingSchedule) {
        this.pingSchedule = pingSchedule;
    }
}
