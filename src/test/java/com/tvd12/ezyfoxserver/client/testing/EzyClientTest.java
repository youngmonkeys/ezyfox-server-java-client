package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;
import com.tvd12.ezyfoxserver.client.command.EzySetup;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionFailureHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyConnectionSuccessHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyHandshakeHandler;
import com.tvd12.ezyfoxserver.client.request.EzyLoginRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;

public class EzyClientTest {

	public static void main(String[] args) throws Exception {
		EzyClientConfig clientConfig = EzyClientConfig.builder().zoneName("freechat").build();
		EzyClient client = EzyClients.getInstance().newDefaultClient(clientConfig);
		EzySetup setup = client.get(EzySetup.class);
		setup.addEventHandler(EzyEventType.CONNECTION_SUCCESS, new EzyConnectionSuccessHandler());
		setup.addEventHandler(EzyEventType.CONNECTION_FAILURE, new EzyConnectionFailureHandler());
		setup.addDataHandler(EzyCommand.HANDSHAKE, new ExHandshakeEventHandler());
		client.connect("127.0.0.1", 3005);

		while (true) {
			Thread.sleep(3);
			client.processEvents();
		}

	}
}

class ExHandshakeEventHandler extends EzyHandshakeHandler {
	@Override
	protected EzyRequest getLoginRequest() {
		return new EzyLoginRequest("freechat", "dungtv", "123456");
	}
}
