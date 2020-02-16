package com.tvd12.ezyfoxserver.client.socket;

import java.net.InetSocketAddress;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionType;

public class EzyUTSocketClient extends EzyTcpSocketClient {

	protected EzyUdpSocketReader udpSocketReader;
	protected final EzyUdpSocketClient udpClient;
	
	public EzyUTSocketClient() {
		super();
		this.udpClient = new EzyUdpSocketClient(responseApi);
	}
	
	public void udpConnect(int port) {
		udpConnect(host, port);
	}
	
	public void udpConnect(String host, int port) {
		InetSocketAddress serverAddress = new InetSocketAddress(host, port);
		EzyUTSocketWriter sw = ((EzyUTSocketWriter)socketWriter);
		sw.setServerAddress(serverAddress);
		this.udpClient.setSessionId(sessionId);
		this.udpClient.setSessionToken(sessionToken);
		this.udpClient.connectTo(serverAddress);
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
	protected EzySocketWriter newSocketWriter() {
		return new EzyUTSocketWriter();
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
		((EzyUTSocketWriter)socketWriter).setDatagramChannel(udpClient.getDatagramChannel());
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
