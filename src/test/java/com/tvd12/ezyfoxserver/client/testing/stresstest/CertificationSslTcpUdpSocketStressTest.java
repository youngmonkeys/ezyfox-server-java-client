package com.tvd12.ezyfoxserver.client.testing.stresstest;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzySslType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CertificationSslTcpUdpSocketStressTest extends SocketStressTest {

    public static void main(String[] args) {
        new CertificationSslTcpUdpSocketStressTest().test();
    }

    @Override
    protected int clientCount() {
        return 1;
    }

    @Override
    protected boolean useUdp() {
        return true;
    }

    @Override
    protected int testDurationInSecond() {
        return 5 * 60;
    }

    @Override
    protected void decorateConfigBuilder(
        EzyClientConfig.Builder configBuilder
    ) {
        configBuilder
            .enableSSL()
            .sslType(EzySslType.CERTIFICATION)
            .build();
    }

    @Override
    protected EzyClient newClient(EzyClientConfig config) {
        return new EzyUTClient(config);
    }

    @Override
    protected void connect(EzyClient client) {
//        client.connect("127.0.0.1", 3005);
        client.connect("ws.tvd12.com", 3005);
    }
}
