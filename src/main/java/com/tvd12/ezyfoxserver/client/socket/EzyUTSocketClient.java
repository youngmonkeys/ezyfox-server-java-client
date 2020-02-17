package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;

public class EzyUTSocketClient extends EzyTcpSocketClient {

	protected final EzyUdpSocketClient udpClient;
	
	public EzyUTSocketClient() {
		super();
		this.udpClient = new EzyUdpSocketClient(codecFactory);
	}
	
	public void udpConnect(int port) {
		udpConnect(host, port);
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
		this.udpClient.popReadMessages(localMessageQueue);
	}
	
	@Override
	public void onDisconnected(int reason) {
		super.onDisconnected(reason);
		this.udpClient.disconnect();
	}
	
}
