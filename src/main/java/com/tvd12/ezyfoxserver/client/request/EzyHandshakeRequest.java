package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

public class EzyHandshakeRequest implements EzyRequest {
    private static final long serialVersionUID = -119780385072575857L;

    protected final String clientId;
    protected final byte[] clientKey;
    protected final String clientType;
    protected final String clientVersion;
    protected final boolean enableEncryption;
    protected final String token;

    public EzyHandshakeRequest(
        String clientId,
        byte[] clientKey,
        String clientType,
        String clientVersion,
        boolean enableEncryption,
        String token
    ) {
        this.clientId = clientId;
        this.clientKey = clientKey;
        this.clientType = clientType;
        this.clientVersion = clientVersion;
        this.token = token;
        this.enableEncryption = enableEncryption;
    }

    @Override
    public Object getCommand() {
        return EzyCommand.HANDSHAKE;
    }

    @Override
    public EzyData serialize() {
        return EzyEntityFactory.newArrayBuilder()
            .append(clientId)
            .append(clientKey)
            .append(clientType)
            .append(clientVersion)
            .append(enableEncryption)
            .append(token).build();
    }
}
