package com.tvd12.ezyfoxserver.client.testing.socket;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketWriter;
import com.tvd12.test.reflect.MethodInvoker;

public class EzyTcpSocketWriterTest {

	@Test
	public void writeToSocketSuccess() throws Exception {
		// given
		int bytesToRead = new Random().nextInt();
		SocketChannel socketChannel = mock(SocketChannel.class);
		when(socketChannel.write(any(ByteBuffer.class))).thenReturn(bytesToRead);
		
		EzyTcpSocketWriter sut = new EzyTcpSocketWriter();
		sut.setSocket(socketChannel);
		
		// when
		ByteBuffer buffer = ByteBuffer.allocate(1);
		Object actual = MethodInvoker.create()
				.object(sut)
				.method("writeToSocket")
				.param(ByteBuffer.class, buffer)
				.invoke();
		
		// then
		assert actual.equals(bytesToRead);
	}
	
	@Test
	public void writeToSocketFailed() throws Exception {
		// given
		SocketChannel socketChannel = mock(SocketChannel.class);
		when(socketChannel.write(any(ByteBuffer.class)))
			.thenThrow(new RuntimeException("just test"));
		
		EzyTcpSocketWriter sut = new EzyTcpSocketWriter();
		sut.setSocket(socketChannel);
		
		// when
		ByteBuffer buffer = ByteBuffer.allocate(1);
		Object actual = MethodInvoker.create()
				.object(sut)
				.method("writeToSocket")
				.param(ByteBuffer.class, buffer)
				.invoke();
		
		// then
		assert actual.equals(-1);
	}
	
}
