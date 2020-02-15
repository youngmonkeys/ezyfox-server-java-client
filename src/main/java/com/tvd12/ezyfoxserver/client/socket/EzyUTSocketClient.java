package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionType;

public class EzyUTSocketClient extends EzyTcpSocketClient {

	protected EzyUdpSocketReader udpSocketReader;
	protected final EzyUdpSocketClient udpClient;
	
	public EzyUTSocketClient() {
		super();
		this.udpClient = new EzyUdpSocketClient(responseApi);
	}
	
	public void udpConnect(String host, int port) {
		this.udpClient.setSessionId(sessionId);
		this.udpClient.setSessionToken(sessionToken);
		this.udpClient.connectTo(host, port);
	}
	
	public void udpSendMessage(EzyArray message) {
		this.udpClient.sendMessage(message);
	}
	
	@Override
	protected void popReadMessages() {
		super.popReadMessages();
		this.udpSocketReader.popMessages(localMessageQueue);
	}
	
	@Override
	protected void createAdapters() {
		super.createAdapters();
		this.udpSocketReader = new EzyUdpSocketReader();
	}
	
	@Override
	protected void updateAdapters() {
		super.updateAdapters();
		Object decoder = codecFactory.newDecoder(EzyConnectionType.SOCKET);
        EzySocketDataDecoder socketDataDecoder = new EzySimpleSocketDataDecoder(decoder);
        this.udpSocketReader.setDecoder(socketDataDecoder);
	}
	
	@Override
	protected void startAdapters() {
		super.startAdapters();
		this.udpSocketReader.setDatagramChannel(udpClient.getDatagramChannel());
		this.udpSocketReader.start();
	}
	
	@Override
	protected void clearAdapters() {
		super.clearAdapters();
		this.clearAdapter(udpSocketReader);
		this.udpSocketReader = null;
	}
	
}
