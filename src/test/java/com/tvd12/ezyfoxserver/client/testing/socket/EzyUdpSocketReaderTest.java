package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.socket.EzySocketDataDecoder;
import com.tvd12.ezyfoxserver.client.socket.EzyUdpSocketReader;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyUdpSocketReaderTest {

    @Test
    public void loop() throws Exception {
        // given
        EzySocketDataDecoder decoder = mock(EzySocketDataDecoder.class);
        EzyArray data = EzyEntityArrays.newArray("test");
        when(decoder.decode(any(EzyMessage.class), any(byte[].class))).thenReturn(data);

        EzyUdpSocketReader sut = new EzyUdpSocketReader() {
            @Override
            protected int readSocketData() {
                buffer.put((byte) 0);
                buffer.put(new byte[]{0, 1, 1});
                return 4;
            }
        };
        sut.setDecoder(decoder);

        // when
        // then
        sut.start();
        Thread.sleep(250);
    }
}
