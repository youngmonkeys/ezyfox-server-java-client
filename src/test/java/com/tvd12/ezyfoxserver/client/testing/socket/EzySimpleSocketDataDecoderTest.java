package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.codec.EzyByteToObjectDecoder;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.codec.EzyMessageHeader;
import com.tvd12.ezyfoxserver.client.socket.EzySimpleSocketDataDecoder;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Test
    public void decodeWithRemainingBytes() throws Exception {
        // given
        TwoBytesMessageDecoder decoder = new TwoBytesMessageDecoder();
        EzySimpleSocketDataDecoder sut = new EzySimpleSocketDataDecoder(decoder);
        List<EzyMessage> receivedMessages = new ArrayList<>();

        // when
        sut.decode(new byte[]{1, 2, 3, 4, 5}, receivedMessages::add);
        ByteBuffer remainBuffer = FieldUtil.getFieldValue(sut, "buffer");
        int remainBytes = remainBuffer.remaining();
        sut.decode(new byte[]{6}, receivedMessages::add);

        // then
        Asserts.assertEquals(receivedMessages.size(), 3);
        Asserts.assertEquals(remainBytes, 1);
        Asserts.assertNull(FieldUtil.getFieldValue(sut, "buffer"));
        Asserts.assertEquals(decoder.decodedBytes.size(), 3);
        Asserts.assertEquals(
            Arrays.toString(decoder.decodedBytes.get(0)),
            Arrays.toString(new byte[]{1, 2})
        );
        Asserts.assertEquals(
            Arrays.toString(decoder.decodedBytes.get(1)),
            Arrays.toString(new byte[]{3, 4})
        );
        Asserts.assertEquals(
            Arrays.toString(decoder.decodedBytes.get(2)),
            Arrays.toString(new byte[]{5, 6})
        );
    }

    @Test
    public void decodeWhenInactive() throws Exception {
        // given
        TwoBytesMessageDecoder decoder = new TwoBytesMessageDecoder();
        EzySimpleSocketDataDecoder sut = new EzySimpleSocketDataDecoder(decoder);
        FieldUtil.setFieldValue(sut, "active", false);
        List<EzyMessage> receivedMessages = new ArrayList<>();

        // when
        sut.decode(new byte[]{1, 2}, receivedMessages::add);

        // then
        Asserts.assertTrue(receivedMessages.isEmpty());
        Asserts.assertEquals(decoder.decodedBytes.size(), 1);
    }

    private static class TwoBytesMessageDecoder implements EzyByteToObjectDecoder {

        private final List<byte[]> decodedBytes = new ArrayList<>();

        @Override
        public Object decode(EzyMessage message) {
            return null;
        }

        @Override
        public void decode(ByteBuffer bytes, Queue<EzyMessage> queue) {
            if (bytes.remaining() < 2) {
                return;
            }
            byte[] messageBytes = new byte[2];
            bytes.get(messageBytes);
            decodedBytes.add(messageBytes);
            queue.offer(mock(EzyMessage.class));
        }

        @Override
        public void reset() {}
    }
}
