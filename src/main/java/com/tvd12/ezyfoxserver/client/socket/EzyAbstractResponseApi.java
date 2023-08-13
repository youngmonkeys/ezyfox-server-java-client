package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.entity.EzyArray;

public abstract class EzyAbstractResponseApi implements EzyResponseApi {

    private final EzyPacketQueue packetQueue;

    public EzyAbstractResponseApi(EzyPacketQueue packetQueue) {
        this.packetQueue = packetQueue;
    }

    @Override
    public void response(EzyPackage pack) throws Exception {
        Object bytes = pack.isEncrypted()
            ? encodeData(pack.getData(), pack.getEncryptionKey())
            : encodeData(pack.getData());
        EzyPacket packet = createPacket(pack.getTransportType(), bytes);
        packetQueue.add(packet);
    }

    private EzyPacket createPacket(EzyConstant transportType, Object bytes) {
        EzySimplePacket packet = new EzySimplePacket();
        packet.setTransportType(transportType);
        packet.setData(bytes);
        return packet;
    }

    protected abstract Object encodeData(EzyArray data) throws Exception;

    protected abstract Object encodeData(EzyArray data, byte[] encryptionKey) throws Exception;
}
