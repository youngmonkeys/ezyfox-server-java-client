package com.tvd12.ezyfoxserver.client.socket;

import lombok.Setter;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class EzyTcpSslSocketReader extends EzySocketReader {

    @Setter
    private InputStream inputStream;

    @Override
    protected ByteBuffer newBuffer(int readBufferSize) {
        return ByteBuffer.allocate(readBufferSize);
    }

    @Override
    protected int readSocketData() {
        try {
            byte[] in = buffer.array();
            int readBytes = inputStream.read(in);
            if (readBytes < 0) {
                return -1;
            }
            buffer.put(in, 0, readBytes);
            return readBytes;
        } catch (Exception e) {
            logger.warn("I/O error at socket-reader", e);
            return -1;
        }
    }
}
