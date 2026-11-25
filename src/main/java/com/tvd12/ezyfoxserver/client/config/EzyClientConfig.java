package com.tvd12.ezyfoxserver.client.config;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyfoxserver.client.constant.EzySslType;
import lombok.Getter;

import javax.net.ssl.X509TrustManager;

@Getter
public class EzyClientConfig implements EzySocketClientConfig {

    private final String zoneName;
    private final String clientName;
    private final EzyPingConfig ping;
    private final boolean enableSSL;
    private final EzySslType sslType;
    private final boolean enableDebug;
    private final boolean verifySslHandshake;
    private final String sslHandshakeHostName;
    private final X509TrustManager sslHandshakeTrustManager;
    private final EzyReconnectConfig reconnect;

    protected EzyClientConfig(Builder builder) {
        this.zoneName = builder.zoneName;
        this.clientName = builder.clientName;
        this.enableSSL = builder.enableSSL;
        this.sslType = builder.sslType;
        this.enableDebug = builder.enableDebug;
        this.verifySslHandshake = builder.verifySslHandshake;
        this.sslHandshakeHostName = builder.sslHandshakeHostName;
        this.sslHandshakeTrustManager = builder.sslHandshakeTrustManager;
        this.ping = builder.pingConfigBuilder.build();
        this.reconnect = builder.reconnectConfigBuilder.build();
    }

    public String getClientName() {
        if (clientName == null) {
            return zoneName;
        }
        return clientName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<EzyClientConfig> {

        private final EzyPingConfig.Builder pingConfigBuilder;
        private final EzyReconnectConfig.Builder reconnectConfigBuilder;
        private String zoneName;
        private String clientName;
        private boolean enableSSL;
        private EzySslType sslType;
        private boolean enableDebug;
        private boolean verifySslHandshake;
        private String sslHandshakeHostName;
        private X509TrustManager sslHandshakeTrustManager;

        public Builder() {
            this.sslType = EzySslType.CUSTOMIZATION;
            this.pingConfigBuilder = new EzyPingConfig.Builder(this);
            this.reconnectConfigBuilder = new EzyReconnectConfig.Builder(this);
        }

        public Builder zoneName(String zoneName) {
            this.zoneName = zoneName;
            return this;
        }

        public Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public Builder enableSSL() {
            return enableSSL(true);
        }

        public Builder enableSSL(boolean enableSSL) {
            this.enableSSL = enableSSL;
            return this;
        }

        public Builder sslType(EzySslType sslType) {
            this.sslType = sslType;
            return this;
        }

        public Builder enableDebug() {
            return enableDebug(true);
        }

        public Builder enableDebug(boolean enableDebug) {
            this.enableDebug = enableDebug;
            return this;
        }

        public Builder verifySslHandshake() {
            return verifySslHandshake(true);
        }

        public Builder verifySslHandshake(boolean verifySslHandshake) {
            this.verifySslHandshake = verifySslHandshake;
            return this;
        }

        public Builder sslHandshakeHostName(String sslHandshakeHostName) {
            this.sslHandshakeHostName = sslHandshakeHostName;
            return this;
        }

        public Builder sslHandshakeTrustManager(X509TrustManager sslHandshakeTrustManager) {
            this.sslHandshakeTrustManager = sslHandshakeTrustManager;
            return this;
        }

        public EzyPingConfig.Builder pingConfigBuilder() {
            return pingConfigBuilder;
        }

        public EzyReconnectConfig.Builder reconnectConfigBuilder() {
            return reconnectConfigBuilder;
        }

        @Override
        public EzyClientConfig build() {
            return new EzyClientConfig(this);
        }
    }
}
