package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionFailureHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.setup.EzyAppSetup;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;

public class EzyClientUseEventLoopAndScheduledClientsTest {

    public static void main(String[] args) {
        EzyClientConfig clientConfig = EzyClientConfig.builder().clientName("first").zoneName("example").build();
        EzyEventLoopGroup eventLoopGroup = new EzyEventLoopGroup(1);
        Runtime.getRuntime().addShutdownHook(new Thread(eventLoopGroup::shutdown));
        EzyClients clients = EzyClients.getInstance();
        EzyClient client = new EzyUTClient(clientConfig, eventLoopGroup);
        clients.addClient(client);
        EzySetup setup = client.setup();
        setup.addEventHandler(EzyEventType.CONNECTION_SUCCESS, new EzyConnectionSuccessHandler());
        setup.addEventHandler(EzyEventType.CONNECTION_FAILURE, new EzyConnectionFailureHandler());
        setup.addDataHandler(EzyCommand.HANDSHAKE, new ExHandshakeEventHandler());
        setup.addDataHandler(EzyCommand.LOGIN, new ExLoginSuccessHandler());
        setup.addDataHandler(EzyCommand.APP_ACCESS, new ExAccessAppHandler());
        setup.addDataHandler(EzyCommand.UDP_HANDSHAKE, new UdpHandshakeHandler());

        EzyAppSetup appSetup = setup.setupApp("hello-world");
        appSetup.addDataHandler("broadcastMessage", new MessageResponseHandler());

//		client.connect("ws.tvd12.com", 3005);
        client.connect("127.0.0.1", 3005);

        clients.startProcessEventsWithScheduledExecutor();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
