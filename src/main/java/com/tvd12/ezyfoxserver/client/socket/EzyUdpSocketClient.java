package com.tvd12.ezyfoxserver.client.socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;

public class EzyUdpSocketClient extends EzyLoggable implements EzyISocketClient {

	protected long sessionId;
	protected String sessionToken;
	protected InetSocketAddress serverAddress;
	protected final DatagramChannel datagramChannel;
	protected final EzyResponseApi responseApi;
	
	public EzyUdpSocketClient(EzyResponseApi responseApi) {
		try {
			this.responseApi = responseApi;
			this.datagramChannel = DatagramChannel.open();
			this.datagramChannel.bind(null);
		}
		catch (Exception e) {
			throw new IllegalStateException("can't create datagram channel", e);
		}
	}
	
	@Override
	public void connectTo(String host, int port) {
		try {
			serverAddress = new InetSocketAddress(host, port);
			sendHandshakeRequest();
		}
		catch (Exception e) {
			throw new IllegalStateException("udp can't connect to: " + serverAddress,  e);
		}
	}

	@Override
    public void sendMessage(EzyArray message) {
        EzyPackage pack = new EzySimplePackage(message, EzyTransportType.UDP);
        try {
            responseApi.response(pack);
        }
        catch (Exception e) {
            logger.warn("send message: " + message + " error", e);
        }
    }

	protected void sendHandshakeRequest() throws Exception {
		int tokenSize = sessionToken.length();
		int messageSize = 0;
		messageSize += 8; // sessionIdSize
		messageSize += 2; // tokenLengthSize
		messageSize += tokenSize; // messageSize
		ByteBuffer buffer = ByteBuffer.allocate(1 + 2 + messageSize);
		byte header = 0;
		header |= 1 << 5;
		buffer.put(header);
		buffer.putShort((short)messageSize);
		buffer.putLong(sessionId);
		buffer.putShort((short)tokenSize);
		buffer.put(sessionToken.getBytes());
		buffer.flip();
		datagramChannel.send(buffer, serverAddress);
	}
	
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}
}
