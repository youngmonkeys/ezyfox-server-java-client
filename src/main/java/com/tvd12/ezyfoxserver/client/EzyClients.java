package com.tvd12.ezyfoxserver.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;

/**
 * Created by tavandung12 on 10/2/18.
 */

public final class EzyClients {

	private String defaultClientName;
    private final Map<String, EzyClient> clients;
    private final static EzyClients INSTANCE = new EzyClients();

    private EzyClients() {
        this.clients = new HashMap<>();
    }

    public static EzyClients getInstance() {
        return INSTANCE;
    }
    
    public EzyClient newClient(EzyClientConfig config) {
    	return newClient(EzyTransportType.TCP, config);
    }

    public EzyClient newClient(
    		EzyTransportType transportType, 
    		EzyClientConfig config) {
        synchronized (clients) {
            return newClient0(transportType, config);
        }
    }

    protected EzyClient newClient0(
    		EzyTransportType transportType,
    		EzyClientConfig config) {
        String clientName = config.getClientName();
        EzyClient client = clients.get(clientName);
        if(client == null) {
        	if(transportType == EzyTransportType.TCP)
        		client = new EzyTcpClient(config);
        	else
        		client = new EzyUTClient(config);
            addClient0(client);
            if (defaultClientName == null)
                defaultClientName = client.getName();
        }
        return client;
    }
    
    public EzyClient newDefaultClient(EzyClientConfig config) {
    	return newDefaultClient(EzyTransportType.TCP, config);
    }

    public EzyClient newDefaultClient(
    		EzyTransportType transportType,
    		EzyClientConfig config) {
        synchronized (clients) {
            EzyClient client = newClient0(transportType, config);
            defaultClientName = client.getName();
            return client;
        }
    }

    public void addClient(EzyClient client) {
        synchronized (clients) {
            addClient0(client);
        }
    }

    protected void addClient0(EzyClient client) {
        this.clients.put(client.getName(), client);
    }

    public EzyClient getClient(String name) {
        synchronized (clients) {
            return getClient0(name);
        }
    }

    protected EzyClient getClient0(String name) {
        if(name == null)
            throw new NullPointerException("can not get client with name: null");
        EzyClient client = clients.get(name);
        return client;
    }

    public EzyClient getDefaultClient() {
        synchronized (clients) {
            if (defaultClientName == null)
                return null;
            EzyClient client = getClient0(defaultClientName);
            return client;
        }
    }

    public void getClients(List<EzyClient> cachedClients) {
        cachedClients.clear();
        synchronized (clients) {
            cachedClients.addAll(clients.values());
        }
    }

}
