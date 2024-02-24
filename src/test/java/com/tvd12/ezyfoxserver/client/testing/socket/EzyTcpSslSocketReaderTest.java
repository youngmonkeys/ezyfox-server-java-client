package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSslSocketReader;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EzyTcpSslSocketReaderTest {

    @Test
    public void readSocketDataReturnNegative1() throws Exception{
        // given
        EzyTcpSslSocketReader instance = new EzyTcpSslSocketReader();

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any(byte[].class))).thenReturn(-1);
        instance.setInputStream(inputStream);

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        instance.setEventLoopGroup(eventLoopGroup);

        // when
        instance.start();
        instance.call();

        // then
        verify(inputStream, times(1)).read(any(byte[].class));
        instance.stop();
    }
}
