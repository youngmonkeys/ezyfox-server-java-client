package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.codec.EzyObjectToByteEncoder;

public class EzySimpleSocketDataEncoder implements EzySocketDataEncoder {

	private EzyObjectToByteEncoder encoder;
	
	public EzySimpleSocketDataEncoder(Object encoder) {
		this.encoder = (EzyObjectToByteEncoder)encoder;
	}
	
	@Override
	public byte[] encode(Object data) throws Exception {
		byte[] bytes = encoder.encode(data);
		return bytes;
	}
	
	@Override
	public byte[] encode(Object data, byte[] encryptionKey) throws Exception {
		byte[] messageContent = encoder.toMessageContent(data);
		return encoder.encryptMessageContent(messageContent, encryptionKey);
	}

}
