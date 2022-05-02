package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.callback.EzyCallback;
import com.tvd12.ezyfox.codec.EzyByteToObjectDecoder;
import com.tvd12.ezyfox.codec.EzyMessage;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;


public class EzySimpleSocketDataDecoder implements EzySocketDataDecoder {

    private final Queue<EzyMessage> queue;
    private final EzyByteToObjectDecoder decoder;
    protected ByteBuffer buffer;
    protected volatile boolean active;

    public EzySimpleSocketDataDecoder(Object decoder) {
        this.active = true;
        this.queue = new LinkedList<>();
        this.decoder = (EzyByteToObjectDecoder) decoder;
    }

    @Override
    public Object decode(EzyMessage message, byte[] encryptionKey) throws Exception {
        return decoder.decode(message, encryptionKey);
    }

    @Override
    public void decode(
        byte[] bytes, EzyCallback<EzyMessage> callback) throws Exception {
        preDecode(bytes);
        decoder.decode(buffer, queue);
        handleQueue(callback);
        postDecode();
    }

    private void handleQueue(EzyCallback<EzyMessage> callback) throws Exception {
        while (!queue.isEmpty() && active) {
            do {
                callback.call(queue.poll());
            } while (!queue.isEmpty());

            if (buffer.hasRemaining()) {
                decoder.decode(buffer, queue);
            }
        }
    }

    private void preDecode(byte[] bytes) {
        if (buffer == null) {
            buffer = newBuffer(bytes);
        } else {
            buffer = mergeBytes(bytes);
        }
    }

    private void postDecode() {
        buffer = getRemainBytes(buffer);
    }

    private ByteBuffer newBuffer(byte[] bytes) {
        return ByteBuffer.wrap(bytes);
    }

    private ByteBuffer mergeBytes(byte[] bytes) {
        int capacity = buffer.remaining() + bytes.length;
        ByteBuffer merge = ByteBuffer.allocate(capacity).put(buffer).put(bytes);
        merge.flip();
        return merge;
    }

    private ByteBuffer getRemainBytes(ByteBuffer old) {
        if (!old.hasRemaining()) {
            return null;
        }
        byte[] bytes = new byte[old.remaining()];
        old.get(bytes);
        return ByteBuffer.wrap(bytes);
    }
}
