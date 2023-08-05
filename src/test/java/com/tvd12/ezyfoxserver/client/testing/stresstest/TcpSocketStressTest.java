package com.tvd12.ezyfoxserver.client.testing.stresstest;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyTcpClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TcpSocketStressTest extends SocketStressTest {

    public static void main(String[] args) {
        new TcpSocketStressTest().test();
    }

    @Override
    protected int clientCount() {
        return 10;
    }

    @Override
    protected int testDurationInSecond() {
        return 5 * 60;
    }

    @Override
    protected EzyClient newClient(
        EzyClientConfig config
    ) {
        return new EzyTcpClient(config);
    }

    @Override
    protected void connect(EzyClient client) {
        client.connect("127.0.0.1", 3005);
//        client.connect("ws.tvd12.com", 3005);
    }
}
