package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.callback.EzyCallback;
import com.tvd12.ezyfox.codec.EzyMessage;

/**
 * Created by tavandung12 on 9/21/18.
 */

public interface EzySocketDataDecoder {

    Object decode(EzyMessage message, byte[] encryptionKey) throws Exception;

    void decode(byte[] bytes, EzyCallback<EzyMessage> callback) throws Exception;

}
