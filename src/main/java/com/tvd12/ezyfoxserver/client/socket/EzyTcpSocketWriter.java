package com.tvd12.ezyfoxserver.client.socket;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EzyTcpSocketWriter extends EzySocketWriter {

    protected SocketChannel socket;

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    @Override
    protected int writeToSocket(ByteBuffer buffer) {
        try {
            int writtenBytes = 0;
            while (buffer.hasRemaining()) {
                writtenBytes += socket.write(buffer);
            }
            return writtenBytes;
        } catch (Throwable e) {
            logger.info("I/O error at socket-writer", e);
            return -1;
        }
    }
}
