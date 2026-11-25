package com.tvd12.ezyfoxserver.client.socket;

public interface EzySocketDataEncoder {

    byte[] encode(Object data) throws Exception;

    byte[] encode(Object data, byte[] encryptionKey) throws Exception;

    default byte[] encode(
        Object data,
        byte[] encryptionKey,
        int compressionThreshold
    ) throws Exception {
        return encryptionKey != null
            ? encode(data, encryptionKey)
            : encode(data);
    }
}
