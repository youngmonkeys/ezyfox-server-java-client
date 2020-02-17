package com.tvd12.ezyfoxserver.client.socket;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class EzyUdpSocketWriter extends EzySocketWriter {

	protected DatagramChannel datagramChannel;
	
	public void setDatagramChannel(DatagramChannel datagramChannel) {
		this.datagramChannel = datagramChannel;
	}
	
	@Override
	protected int writePacketToSocket(EzyPacket packet) throws Exception {
		try {
			byte[] bytes = (byte[])packet.getData();
			int bytesToWrite = bytes.length;
			ByteBuffer buffer = getWriteBuffer((ByteBuffer)writeBuffer, bytesToWrite);
			buffer.clear();
			buffer.put(bytes);
			buffer.flip();
			int writtenByes = datagramChannel.write(buffer);
			return writtenByes;
		}
		catch (Exception e) {
        	logger.warn("I/O error at socket-writer", e);
            return -1;
        }
		finally {
			packet.release();
		}
	}
	
}
