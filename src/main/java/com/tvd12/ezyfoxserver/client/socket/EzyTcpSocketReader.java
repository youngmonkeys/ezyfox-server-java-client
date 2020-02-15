package com.tvd12.ezyfoxserver.client.socket;

import java.nio.channels.SocketChannel;

public class EzyTcpSocketReader extends EzySocketReader {

    protected SocketChannel socket;

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    @Override
    protected int readSocketData() {
        try {
            int bytesToRead = socket.read(buffer);
            return bytesToRead;
        }
        catch (Exception e) {
        	logger.warn("I/O error at socket-reader", e);
            return -1;
        }
    }
}
