package com.tvd12.ezyfoxserver.client.testing.socket;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.Queue;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;

import com.tvd12.ezyfox.callback.EzyCallback;
import com.tvd12.ezyfox.codec.EzyByteToObjectDecoder;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfoxserver.client.socket.EzySimpleSocketDataDecoder;

public class EzySimpleSocketDataDecoderTest {

	@Test
	public void decodeMessage() throws Exception {
		// given
		EzyByteToObjectDecoder decoder = mock(EzyByteToObjectDecoder.class);
		EzySimpleSocketDataDecoder sut = new EzySimpleSocketDataDecoder(decoder);
		
		EzyMessage message = mock(EzyMessage.class);
		byte[] bytes = new byte[] {1, 2, 3};
		when(decoder.decode(message)).thenReturn(bytes);
		
		// when
		Object actual = sut.decode(message, null);
		
		// then
		assert actual == bytes;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void decodeWithCallback() throws Exception {
		// given
		EzyByteToObjectDecoder decoder = mock(EzyByteToObjectDecoder.class);
		EzySimpleSocketDataDecoder sut = new EzySimpleSocketDataDecoder(decoder);
		
		byte[] bytes = new byte[0];
		
		doAnswer(new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Queue<EzyMessage> messageQueue = invocation.getArgumentAt(1, Queue.class);
				EzyMessage message1 = mock(EzyMessage.class);
				EzyMessage message2 = mock(EzyMessage.class);
				messageQueue.offer(message1);
				messageQueue.offer(message2);
				return null;
			}
		})
		.when(decoder)
		.decode(any(ByteBuffer.class), any(Queue.class));
		
		// when
		// then
		sut.decode(bytes, new EzyCallback<EzyMessage>() {
			@Override
			public void call(EzyMessage data) {
			}
		});
	}
	
}
