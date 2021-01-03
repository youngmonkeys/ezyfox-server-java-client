package com.tvd12.ezyfoxserver.client.testing.socket;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.codec.EzySimpleCodecFactory;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketClient;

@SuppressWarnings("resource")
public class EzyUdpSocketClientTest {

	@Test
	public void connectTo() throws Exception {
		// given
		String host = "localhost";
		int port = 10000 + new Random().nextInt(10000);
		startUdpServer(port);
		EzySimpleCodecFactory codecFactory = new EzySimpleCodecFactory();
		EzyUdpSocketClient sut = new EzyUdpSocketClient(codecFactory);
		String sessionToken = "testSessionToken";
		long sessionId = new Random().nextLong();
		sut.setSessionId(sessionId);
		sut.setSessionToken(sessionToken);
		
		// when
		// then
		sut.connectTo(host, port);
	}
	
	@Test
	public void connectToAndTryReconnect() throws Exception {
		// given
		String host = "localhost";
		int port = 10000 + new Random().nextInt(10000);
		startUdpServer(port);
		EzySimpleCodecFactory codecFactory = new EzySimpleCodecFactory();
		EzyUdpSocketClient sut = new EzyUdpSocketClient(codecFactory) {
			@Override
			protected int sleepTimeBeforeReconnect() {
				return 100;
			}
		};
		String sessionToken = "testSessionToken";
		long sessionId = new Random().nextLong();
		sut.setSessionId(sessionId);
		sut.setSessionToken(sessionToken);
		
		// when
		// then
		sut.connectTo(host, port);
		Thread.sleep(350);
	}
	
	private void startUdpServer(int port) throws Exception {
		DatagramChannel datagramChannel = DatagramChannel.open();
	    datagramChannel.configureBlocking(false);
	    datagramChannel.socket().bind(new InetSocketAddress("0.0.0.0", port));
	    datagramChannel.socket().setReuseAddress(true);
		Selector udpSelector = Selector.open();
		datagramChannel.register(udpSelector, SelectionKey.OP_READ);
	}
	
}
