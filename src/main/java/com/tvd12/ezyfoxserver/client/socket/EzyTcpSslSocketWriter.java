package com.tvd12.ezyfoxserver.client.socket;

import java.io.OutputStream;

public class EzyTcpSslSocketWriter extends EzySocketWriter {

    private OutputStream outputStream;

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    protected int writePacketToSocket(EzyPacket packet) {
        byte[] bytes = getBytesToWrite(packet);
        try {
            outputStream.write(bytes);
            return bytes.length;
        } catch (Exception e) {
            logger.warn("I/O error at socket-writer", e);
            return -1;
        } finally {
            packet.release();
        }
    }
}
