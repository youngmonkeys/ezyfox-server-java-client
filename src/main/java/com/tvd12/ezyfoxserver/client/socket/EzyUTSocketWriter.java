package com.tvd12.ezyfoxserver.client.socket;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;

public class EzyUTSocketWriter extends EzyTcpSocketWriter {

	protected SocketAddress serverAddress;
	protected DatagramChannel datagramChannel;
	
	public void setDatagramChannel(DatagramChannel datagramChannel) {
		this.datagramChannel = datagramChannel;
	}
	
	public void setServerAddress(SocketAddress serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	@Override
	protected int writePacketToSocket(EzyPacket packet) throws Exception {
		if(packet.getTransportType() == EzyTransportType.TCP)
			return super.writePacketToSocket(packet);
		return writeUdpPacketToSocket(packet);
	}
	
	protected int writeUdpPacketToSocket(EzyPacket packet) throws Exception {
		try {
			byte[] bytes = (byte[])packet.getData();
			int bytesToWrite = bytes.length;
			ByteBuffer buffer = getWriteBuffer((ByteBuffer)writeBuffer, bytesToWrite);
			buffer.clear();
			buffer.put(bytes);
			buffer.flip();
			int writtenByes = datagramChannel.send(buffer, serverAddress);
			return writtenByes;
		}
		finally {
			packet.release();
		}
	}
	
}
