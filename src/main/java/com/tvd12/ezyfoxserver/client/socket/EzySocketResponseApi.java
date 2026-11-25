package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import lombok.Getter;
import lombok.Setter;

public class EzySocketResponseApi extends EzyAbstractResponseApi {

    protected final EzySocketDataEncoder encoder;
    protected final int compressionThreshold;
    @Getter
    @Setter
    protected volatile boolean compressionEnable;

    public EzySocketResponseApi(EzySocketDataEncoder encoder, EzyPacketQueue packetQueue) {
        this(
            encoder,
            packetQueue,
            EzySocketClientConfig.DEFAULT_COMPRESSION_THRESHOLD
        );
    }

    public EzySocketResponseApi(
        EzySocketDataEncoder encoder,
        EzyPacketQueue packetQueue,
        int compressionThreshold
    ) {
        super(packetQueue);
        this.encoder = encoder;
        this.compressionThreshold = compressionThreshold;
    }

    @Override
    protected Object encodeData(EzyArray data) throws Exception {
        return compressionEnable
            ? encoder.encode(data, null, compressionThreshold)
            : encoder.encode(data);
    }

    @Override
    protected Object encodeData(EzyArray data, byte[] encryptionKey) throws Exception {
        return compressionEnable
            ? encoder.encode(data, encryptionKey, compressionThreshold)
            : encoder.encode(data, encryptionKey);
    }
}
