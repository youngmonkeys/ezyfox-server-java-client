package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.codec.EzyMessageDataEncoder;
import com.tvd12.ezyfox.entity.EzyArray;

public class EzySocketResponseApi extends EzyAbstractResponseApi {

	protected final EzyMessageDataEncoder encoder;

	public EzySocketResponseApi(EzyMessageDataEncoder encoder, EzyPacketQueue packetQueue) {
		super(packetQueue);
		this.encoder = encoder;
	}

	@Override
	protected Object encodeData(EzyArray data) throws Exception {
		Object answer = encoder.encode(data);
		return answer;
	}
	
}
