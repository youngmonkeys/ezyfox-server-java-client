package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionFailedReason;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
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
            if (e instanceof ConnectException) {
                ConnectException c = (ConnectException) e;
                if ("Network is unreachable".equalsIgnoreCase(c.getMessage())) {
                    connectionFailedReason = EzyConnectionFailedReason.NETWORK_UNREACHABLE;
                } else {
                    connectionFailedReason = EzyConnectionFailedReason.CONNECTION_REFUSED;
                }
            } else if (e instanceof UnknownHostException) {
                connectionFailedReason = EzyConnectionFailedReason.UNKNOWN_HOST;
            } else {
                connectionFailedReason = EzyConnectionFailedReason.UNKNOWN;
            }
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
            logger.warn("close socket error", e);
        }
    }
}
