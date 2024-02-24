package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.ssl.EzySslContextFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.OutputStream;

public class EzyTcpSslSocketClient extends EzySocketClient {

    private SSLSocket sslSocket;
    private SSLContext sslContext;
    private InputStream inputStream;
    private OutputStream outputStream;

    public EzyTcpSslSocketClient(EzySocketClientConfig config) {
        super(config);
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected boolean connectNow() {
        try {
            if (sslContext == null) {
                sslContext = EzySslContextFactory
                    .getInstance()
                    .newSslContext();
            }
            SSLSocketFactory clientFactory = sslContext.getSocketFactory();
            sslSocket = (SSLSocket) clientFactory.createSocket(host, port);
            inputStream = sslSocket.getInputStream();
            outputStream = sslSocket.getOutputStream();
            sslSocket.startHandshake();
            return true;
        } catch (Throwable e) {
            processConnectionException(e);
            return false;
        }
    }

    @Override
    protected void resetSocket() {
        this.sslSocket = null;
        this.inputStream = null;
        this.outputStream = null;
    }

    @Override
    protected void closeSocket() {
        try {
            if (sslSocket != null) {
                this.sslSocket.close();
            }
        } catch (Throwable e) {
            logger.info("close socket error");
        }
    }

    @Override
    protected void createAdapters() {
        socketReader = new EzyTcpSslSocketReader();
        socketWriter = new EzyTcpSslSocketWriter();
    }

    @Override
    protected void startAdapters() {
        EzyTcpSslSocketReader sslSocketReader =
            (EzyTcpSslSocketReader) socketReader;
        sslSocketReader.setInputStream(inputStream);
        EzyTcpSslSocketWriter sslSocketWriter =
            (EzyTcpSslSocketWriter) socketWriter;
        sslSocketWriter.setOutputStream(outputStream);

        sslSocketReader.start();
        sslSocketWriter.start();
    }

    @Override
    public void setEventLoopGroup(EzyEventLoopGroup eventLoopGroup) {
        // "currently we haven't supported event loop group
        // with tcp ssl socket yet"
    }
}
