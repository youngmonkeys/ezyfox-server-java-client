package com.tvd12.ezyfoxserver.client.socket;

import static com.tvd12.ezyfoxserver.client.constant.EzySocketStatuses.isSocketConnectable;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.codec.EzyCodecFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionType;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;
import com.tvd12.ezyfoxserver.client.util.EzyValueStack;

public class EzyUdpSocketClient extends EzyLoggable implements EzyISocketClient {

	protected long sessionId;
	protected String sessionToken;
	protected InetSocketAddress serverAddress;
	protected DatagramChannel datagramChannel;
	protected EzyUdpSocketReader socketReader;
	protected EzyUdpSocketWriter socketWriter;
	protected final EzyPacketQueue packetQueue;
	protected final EzyResponseApi responseApi;
	protected final EzyCodecFactory codecFactory;
	protected final EzyValueStack<EzySocketStatus> socketStatuses;
	
	public EzyUdpSocketClient(EzyCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
		this.packetQueue = new EzyBlockingPacketQueue();
		this.responseApi = newResponseApi();
		this.socketStatuses = new EzyValueStack<>(EzySocketStatus.NOT_CONNECT);
	}
	
	private EzyResponseApi newResponseApi() {
        Object encoder = codecFactory.newEncoder(EzyConnectionType.SOCKET);
        EzySocketDataEncoder socketDataEncoder = new EzySimpleSocketDataEncoder(encoder);
        EzyResponseApi api = new EzySocketResponseApi(socketDataEncoder, packetQueue);
        return api;
    }
	
	@Override
	public void connectTo(String host, int port) {
		EzySocketStatus status = socketStatuses.last();
        if (!isSocketConnectable(status)) {
        	logger.warn("udp socket is connecting...");
            return;
        }
		serverAddress = new InetSocketAddress(host, port);
		connect0();
		
	}
	
	@Override
	public boolean reconnect() {
		EzySocketStatus status = socketStatuses.last();
		if (status != EzySocketStatus.CONNECT_FAILED && 
        		status != EzySocketStatus.NOT_CONNECT) {
            return false;
        }
        logger.info("udp socket is re-connecting...");
		connect0();
		return true;
	}
	
	public void setStatus(EzySocketStatus status) {
		socketStatuses.push(status);
	}
	
	protected void connect0() {
		try {
			clearAdapters();
	        createAdapters();
	        updateAdapters();
	        closeSocket();
	        packetQueue.clear();
	        socketStatuses.clear();
	        datagramChannel = DatagramChannel.open();
			datagramChannel.bind(null);
			datagramChannel.connect(serverAddress);
			startAdapters();
			socketStatuses.push(EzySocketStatus.CONNECTING);
			sendHandshakeRequest();
			Thread newThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(sleepTimeBeforeReconnect());
						EzySocketStatus status = socketStatuses.last();
						if(status == EzySocketStatus.CONNECTING)
							socketStatuses.push(EzySocketStatus.CONNECT_FAILED);
						reconnect();
					}
					catch (InterruptedException e) {
						logger.error("udp reconnect interrupted", e);
					}
				}
			});
			newThread.setName("udp-reconnect");
			newThread.start();
		}
		catch (Exception e) {
			throw new IllegalStateException("udp can't connect to: " + serverAddress,  e);
		}
	}
	
	protected int sleepTimeBeforeReconnect() {
		return 3000;
	}
	
	@Override
	public void disconnect(int reason) {
		packetQueue.clear();
        packetQueue.wakeup();
        closeSocket();
		clearAdapters();
		socketStatuses.push(EzySocketStatus.DISCONNECTED);
	}
	
	@Override
	public void close() {
		disconnect(EzyDisconnectReason.CLOSE.getId());
	}

	@Override
    public void sendMessage(EzyArray message) {
        EzyPackage pack = new EzySimplePackage(message, EzyTransportType.UDP);
        try {
            responseApi.response(pack);
        }
        catch (Exception e) {
            logger.warn("udp send message: " + message + " error", e);
        }
    }
	
	public void popReadMessages(List<EzyArray> buffer) {
		EzySocketStatus status = socketStatuses.last();
		if(status == EzySocketStatus.CONNECTING || status == EzySocketStatus.CONNECTED)
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
