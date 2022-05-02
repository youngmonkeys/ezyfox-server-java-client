package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.entity.EzyObject;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.*;
import com.tvd12.ezyfoxserver.client.request.EzyAppAccessRequest;
import com.tvd12.ezyfoxserver.client.request.EzyLoginRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.setup.EzyAppSetup;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;
import com.tvd12.ezyfoxserver.client.socket.EzyMainEventsLoop;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EzyClientTest {

    public static void main(String[] args) {
        EzyClientConfig clientConfig = EzyClientConfig.builder().clientName("first").zoneName("example").build();
        EzyClients clients = EzyClients.getInstance();
        EzyClient client = new EzyUTClient(clientConfig);
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

        // while (true) {
        // Thread.sleep(3);
        // client.processEvents();
        // }

        EzyMainEventsLoop mainEventsLoop = new EzyMainEventsLoop();
        mainEventsLoop.start();
    }
}

class ExHandshakeEventHandler extends EzyHandshakeHandler {
    @Override
    protected EzyRequest getLoginRequest() {
        return new EzyLoginRequest("example", "dungtv", "123456");
    }
}

class ExLoginSuccessHandler extends EzyLoginSuccessHandler {
    @Override
    protected void handleLoginSuccess(EzyData responseData) {
        client.send(new EzyAppAccessRequest("hello-world"));
    }
}

class ExAccessAppHandler extends EzyAppAccessHandler {
    protected void postHandle(EzyApp app, EzyArray data) {
        client.udpConnect(2611);
        sendMessage(app);
    }

    private void sendMessage(EzyApp app) {
        app.send("broadcastMessage", newMessageData());
    }

    private EzyObject newMessageData() {
        return EzyEntityFactory.newObjectBuilder().append("message", "Hi EzyFox, I'm from Java client").build();
    }
}

class MessageResponseHandler implements EzyAppDataHandler<EzyData> {
    public void handle(EzyApp app, EzyData data) {
        System.out.println("received message: " + data);
    }
}

class UdpHandshakeHandler extends EzyUdpHandshakeHandler {

    @Override
    protected void onAuthenticated(EzyArray data) {
        EzyApp app = client.getZone().getApp();
        ScheduledExecutorService executorService = EzyExecutors.newScheduledThreadPool(1, "hellococo");
        executorService.scheduleAtFixedRate(() -> {
            app.udpSend("udpGreet", EzyEntityFactory.newObjectBuilder()
                .append("who", "Dzung")
                .build());
            app.udpSend("udpGreet", EzyEntityFactory.newObjectBuilder()
                .append("who", "Dzung")
                .build());
            app.udpSend("udpGreet", EzyEntityFactory.newObjectBuilder()
                .append("who", "Dzung")
                .build());
        }, 1, 1, TimeUnit.SECONDS);
    }
}