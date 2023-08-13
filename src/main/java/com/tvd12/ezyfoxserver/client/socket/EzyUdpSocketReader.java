package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.codec.EzyMessageReaders;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.concurrent.EzySynchronizedQueue;
import com.tvd12.ezyfoxserver.client.constant.EzySocketConstants;
import com.tvd12.ezyfoxserver.client.util.EzyQueue;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;

public class EzyUdpSocketReader extends EzySocketAdapter {

    protected final ByteBuffer buffer;
    protected final int readBufferSize;
    protected final EzyQueue<EzyArray> dataQueue;
    @Setter
    protected byte[] decryptionKey;
    @Setter
    protected EzySocketDataDecoder decoder;
    @Setter
    protected DatagramChannel datagramChannel;

    public EzyUdpSocketReader() {
        super();
        this.readBufferSize = EzySocketConstants.MAX_READ_BUFFER_SIZE;
        this.dataQueue = new EzySynchronizedQueue<>();
        this.buffer = ByteBuffer.allocateDirect(readBufferSize);
    }

    @Override
    protected void update() {
        while (true) {
            try {
                if (!active) {
                    return;
                }
                this.buffer.clear();
                int bytesToRead = readSocketData();
                if (bytesToRead <= 0) {
                    return;
                }
                buffer.flip();
                byte[] binary = new byte[buffer.limit()];
                buffer.get(binary);
                handleReceivedBytes(binary);
            } catch (InterruptedException e) {
                logger.debug("socket reader interrupted", e);
                return;
            } catch (Throwable e) {
                logger.info("I/O error at socket-reader", e);
                return;
            }
        }
    }

    protected int readSocketData() throws Exception {
        try {
            datagramChannel.receive(buffer);
            return buffer.position();
        } catch (Throwable e) {
            handleSocketReaderException(e);
            return -1;
        }
    }

    protected void handleReceivedBytes(byte[] bytes) {
        EzyMessage message = EzyMessageReaders.bytesToMessage(bytes);
        if (message == null) {
            return;
        }
        onMessageReceived(message);
    }

    @Override
    protected void clear() {
        if (dataQueue != null) {
            dataQueue.clear();
        }
    }

    public void popMessages(List<EzyArray> buffer) {
        dataQueue.pollAll(buffer);
    }

    private void onMessageReceived(EzyMessage message) {
        try {
            Object data = decoder.decode(message, decryptionKey);
            dataQueue.add((EzyArray) data);
        } catch (Throwable e) {
            logger.info("decode error at socket-reader", e);
        }
    }

    @Override
    protected String getThreadName() {
        return "udp-socket-reader";
    }
}
