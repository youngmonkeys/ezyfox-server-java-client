package com.tvd12.ezyfoxserver.client;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyCloseable;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.constant.EzySslType;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.entity.EzyUser;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;

import javax.net.ssl.SSLContext;

public interface EzyClient extends EzyCloseable {

    EzySetup setup();

    void connect(String host, int port);

    boolean reconnect();

    void send(EzyRequest request);

    void send(EzyRequest request, boolean encrypted);

    void send(EzyCommand cmd, EzyArray data);

    void send(EzyCommand cmd, EzyArray data, boolean encrypted);

    void disconnect(int reason);

    default void disconnect() {
        disconnect(EzyDisconnectReason.CLOSE.getId());
    }

    void processEvents();

    void udpConnect(int port);

    void udpConnect(String host, int port);

    void udpSend(EzyRequest request);

    void udpSend(EzyCommand cmd, EzyArray data);

    void close();

    String getName();

    EzyClientConfig getConfig();

    boolean isEnableSSL();

    EzySslType getSslType();

    boolean isEnableEncryption();

    void setSslContext(SSLContext sslContext);

    boolean isEnableDebug();

    EzyUser getMe();

    EzyZone getZone();

    EzyConnectionStatus getStatus();

    void setStatus(EzyConnectionStatus status);

    EzyConnectionStatus getUdpStatus();

    void setUdpStatus(EzyConnectionStatus status);

    long getSessionId();

    void setSessionId(long sessionId);

    String getSessionToken();

    void setSessionToken(String token);

    byte[] getSessionKey();

    void setSessionKey(byte[] sessionKey);

    byte[] getPrivateKey();

    void setPrivateKey(byte[] privateKey);

    byte[] getPublicKey();

    void setPublicKey(byte[] publicKey);

    EzySocketClient getSocket();

    EzyApp getApp();

    EzyApp getAppById(int appId);

    EzyPingManager getPingManager();

    EzyPingSchedule getPingSchedule();

    EzyHandlerManager getHandlerManager();

    default boolean isConnected() {
        return getStatus() == EzyConnectionStatus.CONNECTED;
    }

    default boolean isUdpConnected() {
        return getUdpStatus() == EzyConnectionStatus.CONNECTED;
    }
}
