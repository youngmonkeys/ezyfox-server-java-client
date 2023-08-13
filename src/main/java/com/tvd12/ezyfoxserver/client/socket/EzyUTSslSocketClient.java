package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;

public class EzyUTSslSocketClient extends EzyTcpSslSocketClient {

    protected final EzyUdpSocketClient udpClient;

    public EzyUTSslSocketClient(EzySocketClientConfig config) {
        super(config);
        this.udpClient = new EzyUdpSocketClient(codecFactory);
    }

    public void udpConnect(int port) {
        udpConnect(host, port);
    }

    public void udpConnect(String host, int port) {
        this.udpClient.setSessionId(sessionId);
        this.udpClient.setSessionToken(sessionToken);
        this.udpClient.setSessionKey(sessionKey);
        this.udpClient.connectTo(host, port);
    }

    public void udpSendMessage(EzyArray message) {
        this.udpClient.sendMessage(message, false);
    }

    public void udpSetStatus(EzySocketStatus status) {
        this.udpClient.setStatus(status);
    }

    @Override
    protected void popReadMessages() {
        super.popReadMessages();
        this.udpClient.popReadMessages(localMessageQueue);
    }

    @Override
    protected void clearComponents(int disconnectReason) {
        this.udpClient.disconnect(disconnectReason);
    }

    @Override
    public void disconnect(int reason) {
        super.disconnect(reason);
        this.udpClient.disconnect(reason);
    }

    @Override
    public void close() {
        super.close();
        this.udpClient.close();
    }
}
