package com.tvd12.ezyfoxserver.client;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EzyClients {

    private String defaultClientName;
    private final Map<String, EzyClient> clients;

    private static final EzyClients INSTANCE = new EzyClients();

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

    private EzyClient newClient0(
        EzyTransportType transportType,
        EzyClientConfig config) {
        String clientName = config.getClientName();
        EzyClient client = clients.get(clientName);
        if (client == null) {
            if (transportType == EzyTransportType.TCP) {
                client = new EzyTcpClient(config);
            } else {
                client = new EzyUTClient(config);
            }
            doAddClient(client);
            if (defaultClientName == null) {
                defaultClientName = client.getName();
            }
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
            doAddClient(client);
        }
    }

    private void doAddClient(EzyClient client) {
        this.clients.put(client.getName(), client);
        if (defaultClientName == null) {
            defaultClientName = client.getName();
        }
    }

    public EzyClient getClient(String name) {
        synchronized (clients) {
            return doGetClient(name);
        }
    }

    private EzyClient doGetClient(String name) {
        if (name == null) {
            throw new NullPointerException("can not get client with name: null");
        }
        return clients.get(name);
    }

    public EzyClient getDefaultClient() {
        synchronized (clients) {
            if (defaultClientName == null) {
                return null;
            }
            return doGetClient(defaultClientName);
        }
    }

    public void getClients(List<EzyClient> cachedClients) {
        cachedClients.clear();
        synchronized (clients) {
            cachedClients.addAll(clients.values());
        }
    }

    public void removeClient(String name) {
        synchronized (clients) {
            clients.remove(name);
        }
    }

    public void clear() {
        synchronized (clients) {
            this.clients.clear();
        }
    }
}
