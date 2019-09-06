package com.tvd12.ezyfoxserver.client.testing;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.socket.EzyBlockingPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzyPacket;

public class EzyBlockingPacketQueueTest {

	@Test
	public void test() throws Exception {
		EzyBlockingPacketQueue packetQueue = new EzyBlockingPacketQueue();
		packetQueue.wakeup();
		EzyPacket packet = packetQueue.peek();
		assert packet == null;
	}
	
}
