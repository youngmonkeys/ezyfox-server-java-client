package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfoxserver.client.constant.EzySocketConstants;

import java.nio.ByteBuffer;

public abstract class EzySocketWriter extends EzySocketAdapter {

    protected ByteBuffer writeBuffer;
    protected EzyPacketQueue packetQueue;

    @Override
    protected void loop() {
        this.writeBuffer = ByteBuffer.allocate(EzySocketConstants.MIN_WRITER_BUFFER_SIZE);
        super.loop();
    }

    @Override
    protected void update() {
        while (true) {
            try {
                if (!active) {
                    return;
                }
                EzyPacket packet = packetQueue.peek();
                if (packet == null) {
                    return;
                }
                int writtenBytes = writePacketToSocket(packet);
                if (writtenBytes < 0) {
                    return;
                }
                if (packet.isReleased()) {
                    packetQueue.take();
                } else {
                    packetQueue.again();
                }
            } catch (InterruptedException e) {
                logger.debug("socket-writer thread interrupted", e);
                return;
            } catch (Throwable e) {
                logger.info("problems in socket-writer main loop, thread", e);
            }
        }
    }

    protected int writePacketToSocket(EzyPacket packet) {
        byte[] bytes = getBytesToWrite(packet);
        int bytesToWrite = bytes.length;
        ByteBuffer buffer = getWriteBuffer(writeBuffer, bytesToWrite);
        buffer.clear();
        buffer.put(bytes);
        buffer.flip();
        int bytesWritten = writeToSocket(buffer);
        if (bytesWritten < bytesToWrite) {
            byte[] remainBytes = getPacketFragment(buffer);
            packet.setFragment(remainBytes);
        } else {
            packet.release();
        }
        return bytesWritten;
    }

    protected int writeToSocket(ByteBuffer buffer) {
        return 0;
    }

    protected ByteBuffer getWriteBuffer(ByteBuffer fixed, int bytesToWrite) {
        return bytesToWrite > fixed.capacity() ? ByteBuffer.allocate(bytesToWrite) : fixed;
    }

    private byte[] getPacketFragment(ByteBuffer buffer) {
        byte[] remainBytes = new byte[buffer.remaining()];
        buffer.get(remainBytes);
        return remainBytes;
    }

    protected byte[] getBytesToWrite(EzyPacket packet) {
        return (byte[]) packet.getData();
    }

    public void setPacketQueue(EzyPacketQueue packetQueue) {
        this.packetQueue = packetQueue;
    }

    @Override
    protected String getThreadName() {
        return "ezyfox-socket-writer";
    }
}
