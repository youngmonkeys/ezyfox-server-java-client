package com.tvd12.ezyfoxserver.client.testing.stresstest;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UdpSocketStressTest extends SocketStressTest {

    public static void main(String[] args) {
        new UdpSocketStressTest().test();
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
    protected EzyClient newClient(
        EzyClientConfig config
    ) {
        return new EzyUTClient(config);
    }

    @Override
    protected void connect(EzyClient client) {
//        client.connect("ws.tvd12.com", 3005);
        client.connect("localhost", 3005);
    }
}
