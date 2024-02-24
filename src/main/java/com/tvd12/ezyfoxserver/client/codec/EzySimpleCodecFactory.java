package com.tvd12.ezyfoxserver.client.codec;

import com.tvd12.ezyfox.codec.EzyCodecCreator;
import com.tvd12.ezyfox.codec.MsgPackCodecCreator;
import com.tvd12.ezyfox.constant.EzyConstant;

public class EzySimpleCodecFactory implements EzyCodecFactory {

    private final EzyCodecCreator socketCodecCreator;

    public EzySimpleCodecFactory(boolean enableEncryption) {
        this.socketCodecCreator = new MsgPackCodecCreator(enableEncryption);
    }

    @Override
    public Object newEncoder(EzyConstant connectionType) {
        return socketCodecCreator.newEncoder();
    }

    @Override
    public Object newDecoder(EzyConstant connectionType) {
        return socketCodecCreator.newDecoder(Integer.MAX_VALUE);
    }
}
