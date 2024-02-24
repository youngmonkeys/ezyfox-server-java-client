package com.tvd12.ezyfoxserver.client.testing.stresstest;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UdpSocketStressTestWithEvenLoopGroup extends SocketStressTest {

    public static void main(String[] args) {
        new UdpSocketStressTestWithEvenLoopGroup().test();
    }

    private final EzyEventLoopGroup eventLoopGroup =
        new EzyEventLoopGroup(1);

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
        return 15;
    }

    @Override
    protected EzyClient newClient(
        EzyClientConfig config
    ) {
        return new EzyUTClient(config, eventLoopGroup);
    }

    @Override
    protected void connect(EzyClient client) {
        client.connect("localhost", 3005);
    }

    @Override
    protected void destroy() {
        eventLoopGroup.shutdown();
    }
}
