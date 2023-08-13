package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class EzyTcpSocketClient extends EzySocketClient {

    protected SocketChannel socket;

    public EzyTcpSocketClient(EzySocketClientConfig config) {
        super(config);
    }

    @Override
    protected boolean connectNow() {
        try {
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket = SocketChannel.open();
            socket.connect(socketAddress);
            socket.configureBlocking(true);
            return true;
        } catch (Exception e) {
            processConnectionException(e);
            return false;
        }
    }

    @Override
    protected void createAdapters() {
        socketReader = new EzyTcpSocketReader();
        socketWriter = new EzyTcpSocketWriter();
    }

    @Override
    protected void startAdapters() {
        ((EzyTcpSocketReader) socketReader).setSocket(socket);
        socketReader.start();
        ((EzyTcpSocketWriter) socketWriter).setSocket(socket);
        socketWriter.start();
    }

    @Override
    protected void resetSocket() {
        this.socket = null;
    }

    @Override
    protected void closeSocket() {
        try {
            if (socket != null) {
                this.socket.close();
            }
        } catch (Exception e) {
            logger.info("close socket error", e);
        }
    }
}
