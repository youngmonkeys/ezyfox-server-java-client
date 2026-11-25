package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.security.EzyAsyCrypt;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzyPingScheduleAware;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerificationException;
import com.tvd12.ezyfoxserver.client.ssl.EzySslHandshakeVerifier;

import java.util.Arrays;

public abstract class EzyHandshakeHandler
    extends EzyAbstractDataHandler
    implements EzyPingScheduleAware {

    protected EzyPingSchedule pingSchedule;
    protected EzySslHandshakeVerifier sslHandshakeVerifier;

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
            byte[] encryptedSessionKey = data.get(3, byte[].class, null);
            verifySslHandshake(data, encryptedSessionKey);
            this.client.setSessionKey(decryptSessionKey(encryptedSessionKey));
        }
        EzySocketClient socketClient = client.getSocket();
        if (socketClient != null) {
            socketClient.setCompressionEnable(
                data.get(4, boolean.class, false)
            );
        }
    }

    protected void verifySslHandshake(EzyArray data, byte[] encryptedSessionKey) {
        EzyClientConfig config = client.getConfig();
        if (config == null || !config.isVerifySslHandshake()) {
            return;
        }
        try {
            if (sslHandshakeVerifier == null) {
                sslHandshakeVerifier = newSslHandshakeVerifier(config);
            }
            String hostName = getSslHandshakeHostName(config);
            if (hostName == null) {
                throw new EzySslHandshakeVerificationException(
                    "unknown host name to verify server certificate"
                );
            }
            sslHandshakeVerifier.verify(
                readSslHandshakeProof(data),
                client.getPublicKey(),
                encryptedSessionKey,
                hostName
            );
        } catch (RuntimeException e) {
            client.close();
            throw e;
        }
    }

    protected byte[] readSslHandshakeProof(EzyArray data) {
        Object proof = data.isEmpty() ? null : data.get(0);
        return proof instanceof byte[] ? (byte[]) proof : null;
    }

    protected EzySslHandshakeVerifier newSslHandshakeVerifier(EzyClientConfig config) {
        return new EzySslHandshakeVerifier(config.getSslHandshakeTrustManager());
    }

    protected String getSslHandshakeHostName(EzyClientConfig config) {
        String hostName = config.getSslHandshakeHostName();
        if (hostName != null) {
            return hostName;
        }
        EzySocketClient socketClient = client.getSocket();
        return socketClient != null ? socketClient.getHost() : null;
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
