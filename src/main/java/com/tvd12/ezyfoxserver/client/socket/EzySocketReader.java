package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.callback.EzyCallback;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.concurrent.EzySynchronizedQueue;
import com.tvd12.ezyfoxserver.client.constant.EzySocketConstants;
import com.tvd12.ezyfoxserver.client.util.EzyQueue;

import java.nio.ByteBuffer;
import java.util.List;

public abstract class EzySocketReader extends EzySocketAdapter {

    protected final ByteBuffer buffer;
    protected final EzyQueue<EzyArray> dataQueue;
    protected final int readBufferSize;
    protected final EzyCallback<EzyMessage> decodeBytesCallback;
    protected byte[] sessionKey;
    protected EzySocketDataDecoder decoder;

    public EzySocketReader() {
        super();
        this.readBufferSize = EzySocketConstants.MAX_READ_BUFFER_SIZE;
        this.dataQueue = new EzySynchronizedQueue<>();
        this.buffer = newBuffer(readBufferSize);
        this.decodeBytesCallback = this::onMessageReceived;
    }

    protected ByteBuffer newBuffer(int readBufferSize) {
        return ByteBuffer.allocateDirect(readBufferSize);
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
                decoder.decode(binary, decodeBytesCallback);
            } catch (InterruptedException e) {
                logger.debug("socket reader interrupted", e);
                return;
            } catch (Throwable e) {
                logger.info("I/O error at socket-reader", e);
            }
        }
    }

    @Override
    public boolean call() {
        try {
            if (!active) {
                return false;
            }
            this.buffer.clear();
            int bytesToRead = readSocketData();
            if (bytesToRead < 0) {
                return false;
            }
            if (bytesToRead > 0) {
                buffer.flip();
                byte[] binary = new byte[buffer.limit()];
                buffer.get(binary);
                decoder.decode(binary, decodeBytesCallback);
            }
        } catch (Throwable e) {
            logger.info("I/O error at socket-reader", e);
            return false;
        }
        return true;
    }

    protected abstract int readSocketData();

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
            Object data = decoder.decode(message, sessionKey);
            dataQueue.add((EzyArray) data);
        } catch (Throwable e) {
            logger.info("decode error at socket-reader", e);
        }
    }

    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void setDecoder(EzySocketDataDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    protected String getThreadName() {
        return "ezyfox-socket-reader";
    }
}
