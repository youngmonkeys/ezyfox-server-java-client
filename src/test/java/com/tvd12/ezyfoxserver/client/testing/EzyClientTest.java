package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.entity.EzyObject;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;
import com.tvd12.ezyfoxserver.client.command.EzyAppSetup;
import com.tvd12.ezyfoxserver.client.command.EzySetup;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyAccessAppHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionFailureHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyHandshakeHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginSuccessHandler;
import com.tvd12.ezyfoxserver.client.request.EzyAccessAppRequest;
import com.tvd12.ezyfoxserver.client.request.EzyLoginRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyMainEventsLoop;

public class EzyClientTest {

	public static void main(String[] args) throws Exception {
		EzyClientConfig clientConfig = EzyClientConfig.builder().clientName("first").zoneName("example").build();
		EzyClients clients = EzyClients.getInstance();
		EzyClient client = clients.newDefaultClient(clientConfig);
		EzySetup setup = client.get(EzySetup.class);
		setup.addEventHandler(EzyEventType.CONNECTION_SUCCESS, new EzyConnectionSuccessHandler());
		setup.addEventHandler(EzyEventType.CONNECTION_FAILURE, new EzyConnectionFailureHandler());
		setup.addDataHandler(EzyCommand.HANDSHAKE, new ExHandshakeEventHandler());
		setup.addDataHandler(EzyCommand.LOGIN, new ExLoginSuccessHandler());
		setup.addDataHandler(EzyCommand.APP_ACCESS, new ExAccessAppHandler());

		EzyAppSetup appSetup = setup.setupApp("hello-world");
		appSetup.addDataHandler("broadcastMessage", new MessageResponseHandler());

		client.connect("ws.tvd12.com", 3005);

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
	protected void handleLoginSuccess(EzyArray joinedApps, EzyData responseData) {
		client.send(new EzyAccessAppRequest("hello-world"));
	}
}

class ExAccessAppHandler extends EzyAccessAppHandler {
	protected void postHandle(EzyApp app, EzyArray data) {
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
