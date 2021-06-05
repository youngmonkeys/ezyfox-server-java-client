package com.tvd12.ezyfoxserver.client.socket;

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
		EzyPacket packet = createPacket(bytes, pack);
		packetQueue.add(packet);
	}
	
    private EzyPacket createPacket(Object bytes, EzyPackage pack) {
		EzySimplePacket packet = new EzySimplePacket();
		packet.setTransportType(pack.getTransportType());
		packet.setData(bytes);
		return packet;
    }
	
	protected abstract Object encodeData(EzyArray data) throws Exception;
	
	protected abstract Object encodeData(EzyArray data, byte[] encryptionKey) throws Exception;
	
}
