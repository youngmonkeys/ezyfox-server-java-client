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
            return socket.read(buffer);
        } catch (Throwable e) {
            handleSocketReaderException(e);
            return -1;
        }
    }
}
