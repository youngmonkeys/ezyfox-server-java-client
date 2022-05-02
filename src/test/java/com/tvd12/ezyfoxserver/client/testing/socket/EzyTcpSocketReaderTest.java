package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketReader;
import com.tvd12.test.reflect.MethodInvoker;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EzyTcpSocketReaderTest {

    @Test
    public void readSocketDataSuccess() throws Exception {
        // given
        int bytesToRead = new Random().nextInt();
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.read(any(ByteBuffer.class))).thenReturn(bytesToRead);

        EzyTcpSocketReader sut = new EzyTcpSocketReader();
        sut.setSocket(socketChannel);

        // when
        Object actual = MethodInvoker.create()
            .object(sut)
            .method("readSocketData")
            .invoke();

        // then
        assert actual.equals(bytesToRead);
    }

    @Test
    public void readSocketDataFailed() throws Exception {
        // given
        SocketChannel socketChannel = mock(SocketChannel.class);
        when(socketChannel.read(any(ByteBuffer.class)))
            .thenThrow(new RuntimeException("just test"));

        EzyTcpSocketReader sut = new EzyTcpSocketReader();
        sut.setSocket(socketChannel);

        // when
        Object actual = MethodInvoker.create()
            .object(sut)
            .method("readSocketData")
            .invoke();

        // then
        assert actual.equals(-1);
    }
}
