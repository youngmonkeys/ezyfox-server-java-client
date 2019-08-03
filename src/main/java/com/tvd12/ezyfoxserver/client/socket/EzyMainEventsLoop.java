package com.tvd12.ezyfoxserver.client.socket;

import java.util.ArrayList;
import java.util.List;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;

public class EzyMainEventsLoop extends EzyLoggable {
	
	protected volatile boolean active;
	protected final EzyClients clients;
	protected final List<EzyClient> cachedClients;

	public EzyMainEventsLoop() {
		this.clients = EzyClients.getInstance();
		this.cachedClients = new ArrayList<>();
	}

	public void start() {
		start(3);
	}

	public void start(int sleepTime) {
		this.active = true;
		while (active) {
			processEvents(sleepTime);
		}
	}
	
	protected void processEvents(int sleepTime) {
		try {
			Thread.sleep(sleepTime);
			clients.getClients(cachedClients);
			for (EzyClient one : cachedClients)
				one.processEvents();
		}
		catch (Exception e) {
			logger.warn("process events error", e);
		}
	}

	public void stop() {
		this.active = false;
	}
}
