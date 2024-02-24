package com.tvd12.ezyfoxserver.client.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public final class EzySslContextFactory {

    private static final EzySslContextFactory INSTANCE =
        new EzySslContextFactory();
    private static final String PROTOCOL = "TLS";

    private EzySslContextFactory() {}

    public static EzySslContextFactory getInstance() {
        return INSTANCE;
    }

    public SSLContext newSslContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        TrustManager[] trustManagers = EzySslTrustManagerFactory
            .getInstance()
            .engineGetTrustManagers();
        sslContext.init(
            null,
            trustManagers,
            null
        );
        return sslContext;
    }
}
