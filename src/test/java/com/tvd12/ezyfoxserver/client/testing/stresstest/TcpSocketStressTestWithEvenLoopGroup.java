package com.tvd12.ezyfoxserver.client.testing.stresstest;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyTcpClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TcpSocketStressTestWithEvenLoopGroup extends SocketStressTest {

    public static void main(String[] args) {
        new TcpSocketStressTestWithEvenLoopGroup().test();
    }

    private final EzyEventLoopGroup eventLoopGroup =
        new EzyEventLoopGroup(1);

    @Override
    protected int clientCount() {
        return 10;
    }

    @Override
    protected int testDurationInSecond() {
        return 15;
    }

    @Override
    protected EzyClient newClient(
        EzyClientConfig config
    ) {
        return new EzyTcpClient(config, eventLoopGroup);
    }

    @Override
    protected void connect(EzyClient client) {
        client.connect("127.0.0.1", 3005);
//        client.connect("ws.tvd12.com", 3005);
    }

    @Override
    protected void destroy() {
        eventLoopGroup.shutdown();
    }
}
