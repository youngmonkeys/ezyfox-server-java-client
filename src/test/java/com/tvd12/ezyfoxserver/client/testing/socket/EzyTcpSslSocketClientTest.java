package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSslSocketClient;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.*;

public class EzyTcpSslSocketClientTest {

    @Test
    public void connectTest() {
        // given
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyTcpSslSocketClient instance = new EzyTcpSslSocketClient(
            config
        );
        String host = "host";
        int port = 0;

        // when
        instance.connectTo(host, port);

        // then
        Asserts.assertEquals(instance.getHost(), host);
        Asserts.assertEquals(instance.getPort(), 0);
    }

    @Test
    public void connectWithSslConnectTest() throws Exception {
        // given
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyTcpSslSocketClient instance = new EzyTcpSslSocketClient(
            config
        );
        SSLContext sslContext = mock(SSLContext.class);
        TestSSLContextSpi contextSpi = mock(TestSSLContextSpi.class);
        FieldUtil.setFieldValue(sslContext, "contextSpi", contextSpi);

        SSLSocketFactory sslSocketFactory = mock(SSLSocketFactory.class);
        when(contextSpi.engineGetSocketFactory()).thenReturn(sslSocketFactory);

        String host = "host";
        int port = 0;

        SSLSocket sslSocket = mock(SSLSocket.class);
        when(sslSocketFactory.createSocket(host, port)).thenReturn(
            sslSocket
        );

        InputStream inputStream = mock(InputStream.class);
        when(sslSocket.getInputStream()).thenReturn(inputStream);

        OutputStream outputStream = mock(OutputStream.class);
        when(sslSocket.getOutputStream()).thenReturn(outputStream);

        instance.setSslContext(sslContext);

        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        instance.setPingSchedule(pingSchedule);

        // when
        instance.connectTo(host, port);

        // then
        Asserts.assertEquals(instance.getHost(), host);
        Asserts.assertEquals(instance.getPort(), 0);

        instance.close();
    }

    @Test
    public void connectWithSslConnectAndCloseThrowExceptionTest() throws Exception {
        // given
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzyTcpSslSocketClient instance = new EzyTcpSslSocketClient(
            config
        );
        SSLContext sslContext = mock(SSLContext.class);
        TestSSLContextSpi contextSpi = mock(TestSSLContextSpi.class);
        FieldUtil.setFieldValue(sslContext, "contextSpi", contextSpi);

        SSLSocketFactory sslSocketFactory = mock(SSLSocketFactory.class);
        when(contextSpi.engineGetSocketFactory()).thenReturn(sslSocketFactory);

        String host = "host";
        int port = 0;

        SSLSocket sslSocket = mock(SSLSocket.class);
        when(sslSocketFactory.createSocket(host, port)).thenReturn(
            sslSocket
        );
        doThrow(new RuntimeException("test")).when(sslSocket).close();

        InputStream inputStream = mock(InputStream.class);
        when(sslSocket.getInputStream()).thenReturn(inputStream);

        OutputStream outputStream = mock(OutputStream.class);
        when(sslSocket.getOutputStream()).thenReturn(outputStream);

        instance.setSslContext(sslContext);

        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        instance.setPingSchedule(pingSchedule);

        // when
        instance.connectTo(host, port);

        // then
        Asserts.assertEquals(instance.getHost(), host);
        Asserts.assertEquals(instance.getPort(), 0);

        instance.close();
    }

    @Test
    public void closeSocketTest() throws Exception {
        // given
        TestEzyTcpSslSocketClient instance = new TestEzyTcpSslSocketClient();
        SSLSocket sslSocket = mock(SSLSocket.class);
        FieldUtil.setFieldValue(instance, "sslSocket", sslSocket);

        // when
        instance.close0();

        // then
        verify(sslSocket, times(1)).close();
    }

    @Test
    public void closeSocketThrowExceptionTest() throws Exception {
        // given
        TestEzyTcpSslSocketClient instance = new TestEzyTcpSslSocketClient();
        SSLSocket sslSocket = mock(SSLSocket.class);
        doThrow(new IOException("test")).when(sslSocket).close();
        FieldUtil.setFieldValue(instance, "sslSocket", sslSocket);

        // when
        instance.close0();

        // then
        verify(sslSocket, times(1)).close();
    }

    @Test
    public void closeSocketWhenSocketNullTest() {
        // given
        TestEzyTcpSslSocketClient instance = new TestEzyTcpSslSocketClient();

        // when
        // then
        instance.close0();
    }

    @Test
    public void setEventLoopGroupIgnoredTest() {
        // given
        TestEzyTcpSslSocketClient instance = new TestEzyTcpSslSocketClient();

        // when
        instance.setEventLoopGroup(mock(EzyEventLoopGroup.class));

        // then
        Asserts.assertNull(FieldUtil.getFieldValue(instance, "eventLoopGroup"));
    }

    private static class TestEzyTcpSslSocketClient extends EzyTcpSslSocketClient {

        private TestEzyTcpSslSocketClient() {
            super(mock(EzySocketClientConfig.class));
        }

        private void close0() {
            closeSocket();
        }
    }

    public abstract static class TestSSLContextSpi extends SSLContextSpi {
        @Override
        public abstract SSLSocketFactory engineGetSocketFactory();
    }
}
