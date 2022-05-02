package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;

public class EzySocketResponseApi extends EzyAbstractResponseApi {

    protected final EzySocketDataEncoder encoder;

    public EzySocketResponseApi(EzySocketDataEncoder encoder, EzyPacketQueue packetQueue) {
        super(packetQueue);
        this.encoder = encoder;
    }

    @Override
    protected Object encodeData(EzyArray data) throws Exception {
        return encoder.encode(data);
    }

    @Override
    protected Object encodeData(EzyArray data, byte[] encryptionKey) throws Exception {
        return encoder.encode(data, encryptionKey);
    }
}
