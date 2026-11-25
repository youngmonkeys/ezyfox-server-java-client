package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.callback.EzyCallback;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.socket.EzySocketDataDecoder;
import com.tvd12.ezyfoxserver.client.socket.EzySocketReader;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.*;

public class EzySocketReaderTest extends BaseTest {

    @SuppressWarnings({"unchecked"})
    @Test
    public void loop() throws Exception {
        // given
        EzyMessage message = mock(EzyMessage.class);
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        doAnswer(invocation -> {
            EzyCallback<EzyMessage> callback = invocation.getArgumentAt(1, EzyCallback.class);
            callback.call(message);
            return null;
        })
            .when(decoder).decode(any(byte[].class), any(EzyCallback.class));
        EzySocketReader sut = new EzySocketReader() {
            @Override
            protected int readSocketData() {
                buffer.put((byte) 0);
                buffer.put(new byte[]{1, 1});
                return 3;
            }
        };
        sut.setDecoder(decoder);

        // when
        sut.start();
        Thread.sleep(250L);
        sut.stop();
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void loopUntilNoData() throws Exception {
        // given
        EzyArray data = EzyEntityArrays.newArray("test");
        byte[] sessionKey = new byte[]{1, 2, 3};
        EzySocketDataDecoder decoder = newCallbackDecoder();
        when(decoder.decode(any(EzyMessage.class), eq(sessionKey))).thenReturn(data);
        AtomicInteger readCount = new AtomicInteger();
        EzySocketReader sut = new EzySocketReader() {
            @Override
            protected int readSocketData() {
                if (readCount.incrementAndGet() > 1) {
                    return 0;
                }
                buffer.put(new byte[]{0, 0, 1, 1});
                return 4;
            }
        };
        sut.setDecoder(decoder);
        sut.setSessionKey(sessionKey);

        // when
        sut.start();
        waitStopped(sut);

        // then
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertEquals(buffer.size(), 1);
        Asserts.assertEquals(buffer.get(0), data);
        Asserts.assertEquals(readCount.get(), 2);
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void loopInterrupted() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        doThrow(new InterruptedException("test"))
            .when(decoder).decode(any(byte[].class), any(EzyCallback.class));
        EzySocketReader sut = newReader(1);
        sut.setDecoder(decoder);

        // when
        sut.start();

        // then
        waitStopped(sut);
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void loopErrorThenStop() throws Exception {
        // given
        AtomicInteger decodeCount = new AtomicInteger();
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        EzySocketReader sut = newReader(1);
        doAnswer(invocation -> {
            if (decodeCount.incrementAndGet() > 1) {
                sut.stop();
            }
            throw new IllegalStateException("test");
        })
            .when(decoder).decode(any(byte[].class), any(EzyCallback.class));
        sut.setDecoder(decoder);

        // when
        sut.start();

        // then
        waitStopped(sut);
        Asserts.assertEquals(decodeCount.get(), 2);
    }

    @Test
    public void callWhenInactive() {
        // given
        EzySocketReader sut = newReader(1);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
    }

    @Test
    public void callWhenReadFailed() {
        // given
        EzySocketReader sut = newReader(-1);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        sut.stop();
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void callWhenNoData() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        EzySocketReader sut = newReader(0);
        sut.setDecoder(decoder);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        verify(decoder, times(0)).decode(any(byte[].class), any(EzyCallback.class));
        sut.stop();
    }

    @Test
    public void callWithData() throws Exception {
        // given
        EzyArray data = EzyEntityArrays.newArray("test");
        EzySocketDataDecoder decoder = newCallbackDecoder();
        when(decoder.decode(any(EzyMessage.class), any(byte[].class))).thenReturn(data);
        EzySocketReader sut = newReader(1);
        sut.setDecoder(decoder);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertEquals(buffer.size(), 1);
        Asserts.assertEquals(buffer.get(0), data);
        sut.stop();
    }

    @Test
    public void callWithMessageDecodeError() throws Exception {
        // given
        EzySocketDataDecoder decoder = newCallbackDecoder();
        when(decoder.decode(any(EzyMessage.class), any(byte[].class)))
            .thenThrow(new IllegalStateException("test"));
        EzySocketReader sut = newReader(1);
        sut.setDecoder(decoder);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertTrue(actual);
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertTrue(buffer.isEmpty());
        sut.stop();
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void callWithDecodeBytesError() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        doThrow(new IllegalStateException("test"))
            .when(decoder).decode(any(byte[].class), any(EzyCallback.class));
        EzySocketReader sut = newReader(1);
        sut.setDecoder(decoder);
        startWithEventLoop(sut);

        // when
        boolean actual = sut.call();

        // then
        Asserts.assertFalse(actual);
        sut.stop();
    }

    @Test
    public void stopClearDataQueue() throws Exception {
        // given
        EzyArray data = EzyEntityArrays.newArray("test");
        EzySocketDataDecoder decoder = newCallbackDecoder();
        when(decoder.decode(any(EzyMessage.class), any(byte[].class))).thenReturn(data);
        EzySocketReader sut = newReader(1);
        sut.setDecoder(decoder);
        startWithEventLoop(sut);
        sut.call();

        // when
        sut.stop();

        // then
        List<EzyArray> buffer = new ArrayList<>();
        sut.popMessages(buffer);
        Asserts.assertTrue(buffer.isEmpty());
    }

    @SuppressWarnings({"unchecked"})
    private static EzySocketDataDecoder newCallbackDecoder() throws Exception {
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        doAnswer(invocation -> {
            EzyCallback<EzyMessage> callback = invocation.getArgumentAt(1, EzyCallback.class);
            callback.call(mock(EzyMessage.class));
            return null;
        })
            .when(decoder).decode(any(byte[].class), any(EzyCallback.class));
        return decoder;
    }

    private static EzySocketReader newReader(int readBytes) {
        return new EzySocketReader() {
            @Override
            protected int readSocketData() {
                if (readBytes > 0) {
                    buffer.put(new byte[readBytes]);
                }
                return readBytes;
            }
        };
    }

    private static void startWithEventLoop(EzySocketReader sut) {
        sut.setEventLoopGroup(mock(EzyEventLoopGroup.class));
        sut.start();
    }

    private static void waitStopped(EzySocketReader sut) throws Exception {
        long startTime = System.currentTimeMillis();
        while (!sut.isStopped()) {
            if (System.currentTimeMillis() - startTime > 3000) {
                throw new IllegalStateException("socket reader is not stopped");
            }
            Thread.sleep(10);
        }
    }
}
