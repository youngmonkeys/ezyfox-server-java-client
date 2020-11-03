package com.tvd12.ezyfoxserver.client.testing;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.tvd12.ezyfoxserver.client.handler.EzyAppAccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionFailureHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyHandshakeHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginSuccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyUdpHandshakeHandler;
import com.tvd12.ezyfoxserver.client.request.EzyAppAccessRequest;
import com.tvd12.ezyfoxserver.client.request.EzyLoginRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.setup.EzyAppSetup;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;
import com.tvd12.ezyfoxserver.client.socket.EzyMainEventsLoop;

public class EzyHelloClientTest {

	public static void main(String[] args) throws Exception {
		EzyClientConfig clientConfig = EzyClientConfig.builder()
				.clientName("first")
				.zoneName("example")
				.build();
		EzyClients clients = EzyClients.getInstance();
		EzyClient client = new EzyUTClient(clientConfig);
		clients.addClient(client);
		EzySetup setup = client.setup();
		setup.addEventHandler(EzyEventType.CONNECTION_SUCCESS, new EzyConnectionSuccessHandler());
		setup.addEventHandler(EzyEventType.CONNECTION_FAILURE, new EzyConnectionFailureHandler());
		setup.addDataHandler(EzyCommand.HANDSHAKE, new HelloHandshakeEventHandler());
		setup.addDataHandler(EzyCommand.LOGIN, new HelloLoginSuccessHandler());
		setup.addDataHandler(EzyCommand.APP_ACCESS, new HelloAccessAppHandler());
		setup.addDataHandler(EzyCommand.UDP_HANDSHAKE, new HelloUdpHandshakeHandler());

		EzyAppSetup appSetup = setup.setupApp("hello");
		appSetup.addDataHandler("hello", new HelloMessageResponseHandler());

		client.connect("127.0.0.1", 3005);

		EzyMainEventsLoop mainEventsLoop = new EzyMainEventsLoop();
		mainEventsLoop.start();
	}
}

class HelloHandshakeEventHandler extends EzyHandshakeHandler {
	@Override
	protected EzyRequest getLoginRequest() {
		return new EzyLoginRequest("hello", "dungtv", "123456");
	}
}

class HelloLoginSuccessHandler extends EzyLoginSuccessHandler {
	@Override
	protected void handleLoginSuccess(EzyData responseData) {
		client.send(new EzyAppAccessRequest("hello"));
	}
}

class HelloAccessAppHandler extends EzyAppAccessHandler {
	protected void postHandle(EzyApp app, EzyArray data) {
//		client.udpConnect(2611);
		sendMessage(app);
	}

	private void sendMessage(EzyApp app) {
		app.send("hello", newMessageData());
	}

	private EzyObject newMessageData() {
		return EzyEntityFactory.newObjectBuilder().append("message", "Hi EzyFox, I'm from Java client").build();
	}
}

class HelloMessageResponseHandler implements EzyAppDataHandler<EzyData> {
	public void handle(EzyApp app, EzyData data) {
		System.out.println("received message: " + data);
	}
}

class HelloUdpHandshakeHandler extends EzyUdpHandshakeHandler {

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