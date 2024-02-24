package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.codec.EzyByteToObjectDecoder;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.codec.EzyMessageHeader;
import com.tvd12.ezyfoxserver.client.socket.EzySimpleSocketDataDecoder;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.Queue;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EzySimpleSocketDataDecoderTest extends BaseTest {

    @Test
    public void decodeMessage() throws Exception {
        // given
        EzyByteToObjectDecoder decoder = mock(EzyByteToObjectDecoder.class);
        EzySimpleSocketDataDecoder sut = new EzySimpleSocketDataDecoder(decoder);

        EzyMessage message = mock(EzyMessage.class);
        EzyMessageHeader messageHeader = mock(EzyMessageHeader.class);
        when(message.getHeader()).thenReturn(messageHeader);

        byte[] bytes = new byte[]{1, 2, 3};
        when(decoder.decode(message)).thenReturn(bytes);

        // when
        Object actual = sut.decode(message, null);

        // then
        Asserts.assertEquals(bytes, actual);

        verify(message, times(1)).getHeader();
        verifyNoMoreInteractions(message);

        verify(messageHeader, times(1)).isEncrypted();
        verifyNoMoreInteractions(messageHeader);

        verify(decoder, times(1)).decode(message);
        verifyNoMoreInteractions(decoder);
    }

    @Test
    public void decodeMessageCaseEncrypted() throws Exception {
        // given
        EzyByteToObjectDecoder decoder = mock(EzyByteToObjectDecoder.class);
        EzySimpleSocketDataDecoder sut = new EzySimpleSocketDataDecoder(decoder);

        EzyMessage message = mock(EzyMessage.class);
        EzyMessageHeader messageHeader = mock(EzyMessageHeader.class);
        when(messageHeader.isEncrypted()).thenReturn(true);
        when(message.getHeader()).thenReturn(messageHeader);

        byte[] bytes = new byte[]{1, 2, 3};
        byte[] decryptionKey = RandomUtil.randomShortByteArray();
        when(decoder.decode(message, decryptionKey)).thenReturn(bytes);

        // when
        Object actual = sut.decode(message, decryptionKey);

        // then
        Asserts.assertEquals(bytes, actual);

        verify(message, times(1)).getHeader();
        verifyNoMoreInteractions(message);

        verify(messageHeader, times(1)).isEncrypted();
        verifyNoMoreInteractions(messageHeader);

        verify(decoder, times(1)).decode(message, decryptionKey);
        verifyNoMoreInteractions(decoder);
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void decodeWithCallback() throws Exception {
        // given
        EzyByteToObjectDecoder decoder = mock(EzyByteToObjectDecoder.class);
        EzySimpleSocketDataDecoder sut = new EzySimpleSocketDataDecoder(decoder);

        byte[] bytes = new byte[0];

        doAnswer(invocation -> {
            Queue<EzyMessage> messageQueue = invocation.getArgumentAt(1, Queue.class);
            EzyMessage message1 = mock(EzyMessage.class);
            EzyMessage message2 = mock(EzyMessage.class);
            messageQueue.offer(message1);
            messageQueue.offer(message2);
            return null;
        })
            .when(decoder)
            .decode(any(ByteBuffer.class), any(Queue.class));

        // when
        // then
        sut.decode(bytes, data -> {});
    }
}
