package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.callback.EzyCallback;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfoxserver.client.socket.EzySocketDataDecoder;
import com.tvd12.ezyfoxserver.client.socket.EzySocketReader;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzySocketReaderTest {

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
    }
}
