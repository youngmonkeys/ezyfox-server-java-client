package com.tvd12.ezyfoxserver.client.socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.codec.EzyCodecFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionType;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;

public class EzyUdpSocketClient extends EzyLoggable implements EzyISocketClient {

	protected long sessionId;
	protected String sessionToken;
	protected volatile boolean active;
	protected InetSocketAddress serverAddress;
	protected DatagramChannel datagramChannel;
	protected EzyUdpSocketReader socketReader;
	protected EzyUdpSocketWriter socketWriter;
	protected final EzyPacketQueue packetQueue;
	protected final EzyResponseApi responseApi;
	protected final EzyCodecFactory codecFactory;
	
	public EzyUdpSocketClient(EzyCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
		this.packetQueue = new EzyBlockingPacketQueue();
		this.responseApi = newResponseApi();
	}
	
	private EzyResponseApi newResponseApi() {
        Object encoder = codecFactory.newEncoder(EzyConnectionType.SOCKET);
        EzySocketDataEncoder socketDataEncoder = new EzySimpleSocketDataEncoder(encoder);
        EzyResponseApi api = new EzySocketResponseApi(socketDataEncoder, packetQueue);
        return api;
    }
	
	@Override
	public void connectTo(String host, int port) {
		try {
			clearAdapters();
	        createAdapters();
	        updateAdapters();
	        closeSocket();
			serverAddress = new InetSocketAddress(host, port);
			datagramChannel = DatagramChannel.open();
			datagramChannel.bind(null);
			datagramChannel.connect(serverAddress);
			startAdapters();
			active = true;
			sendHandshakeRequest();
		}
		catch (Exception e) {
			throw new IllegalStateException("udp can't connect to: " + serverAddress,  e);
		}
	}
	
	public void disconnect() {
		packetQueue.clear();
        packetQueue.wakeup();
        closeSocket();
		clearAdapters();
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
	
	public void popReadMessages(List<EzyArray> buffer) {
		if(active)
			this.socketReader.popMessages(buffer);
	}
	
	protected void createAdapters() {
		this.socketReader = new EzyUdpSocketReader();
		this.socketWriter = new EzyUdpSocketWriter();
	}
	
	protected void updateAdapters() {
		Object decoder = codecFactory.newDecoder(EzyConnectionType.SOCKET);
        EzySocketDataDecoder socketDataDecoder = new EzySimpleSocketDataDecoder(decoder);
        this.socketReader.setDecoder(socketDataDecoder);
        this.socketWriter.setPacketQueue(packetQueue);
	}
	
	protected void startAdapters() {
		this.socketReader.setDatagramChannel(datagramChannel);
		this.socketReader.start();
		this.socketWriter.setDatagramChannel(datagramChannel);
		this.socketWriter.start();
	}
	
	protected void clearAdapters() {
		this.clearAdapter(socketReader);
		this.socketReader = null;
		this.clearAdapter(socketWriter);
		this.socketWriter = null;
	}
	
	protected void clearAdapter(EzySocketAdapter adapter) {
        if (adapter != null)
            adapter.stop();
    }
	
	protected void closeSocket() {
		try {
			if(datagramChannel != null)
				datagramChannel.close();
		}
		catch(Exception e) {
			logger.warn("close udp socket error", e);
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
	
}
