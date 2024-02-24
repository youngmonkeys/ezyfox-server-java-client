package com.tvd12.ezyfoxserver.client;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSslSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketConnector;

public class EzyUTClient extends EzyTcpClient {

    public EzyUTClient(EzyClientConfig config) {
        super(config);
    }

    public EzyUTClient(
        EzyClientConfig config,
        EzyEventLoopGroup eventLoopGroup
    ) {
        super(config, eventLoopGroup);
    }

    @Override
    protected EzySocketClient newTcpSocketClient(
        EzySocketClientConfig config
    ) {
        return isEnableCertificationSSL()
            ? new EzyUTSslSocketClient(config)
            : new EzyUTSocketClient(config);
    }

    @Override
    public void udpConnect(int port) {
        ((EzyUdpSocketConnector) socketClient).udpConnect(port);
    }

    @Override
    public void udpConnect(String host, int port) {
        ((EzyUdpSocketConnector) socketClient).udpConnect(host, port);
    }

    @Override
    public void udpSend(EzyRequest request, boolean encrypted) {
        Object cmd = request.getCommand();
        EzyData data = request.serialize();
        udpSend((EzyCommand) cmd, (EzyArray) data, encrypted);
    }

    @Override
    public void udpSend(EzyCommand cmd, EzyArray data, boolean encrypted) {
        EzyArray array = requestSerializer.serialize(cmd, data);
        ((EzyUdpSocketConnector) socketClient).udpSendMessage(array, encrypted);
        printSentData(cmd, data);
    }
}
