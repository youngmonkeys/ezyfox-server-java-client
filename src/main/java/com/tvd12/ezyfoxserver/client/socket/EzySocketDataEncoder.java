package com.tvd12.ezyfoxserver.client.socket;

public interface EzySocketDataEncoder {

    byte[] encode(Object data) throws Exception;

    byte[] encode(Object data, byte[] encryptionKey) throws Exception;
}
