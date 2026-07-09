package com.tvd12.ezyfoxserver.client;

import com.tvd12.ezyfox.concurrent.EzyEventLoopEvent;
import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tvd12.ezyfox.util.EzyProcessor.processWithLogException;
import static com.tvd12.ezyfoxserver.client.constant.EzySocketConstants.PROCESS_EVENTS_PERIOD;

public final class EzyClients extends EzyLoggable {

    private String defaultClientName;
    private final Map<String, EzyClient> clients;
    private ScheduledExecutorService processEventsScheduledExecutorService;
    private EzyEventLoopGroup processEventsEventLoopGroup;
    private EzyEventLoopEvent processEventsLoopEvent;

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
        EzyClientConfig config
    ) {
        synchronized (clients) {
            return doNewClient(transportType, config);
        }
    }

    private EzyClient doNewClient(
        EzyTransportType transportType,
        EzyClientConfig config) {
        String clientName = config.getClientName();
        EzyClient client = clients.get(clientName);
        if (client == null) {
            if (transportType == EzyTransportType.TCP) {
                client = new EzyTcpClient(
                    config,
                    processEventsEventLoopGroup
                );
            } else {
                client = new EzyUTClient(
                    config,
                    processEventsEventLoopGroup
                );
            }
            doAddClient(client);
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
            EzyClient client = doNewClient(transportType, config);
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
            throw new NullPointerException(
                "can not get client with name: null"
            );
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

    public EzyClient removeClient(String name) {
        synchronized (clients) {
            EzyClient client = clients.remove(name);
            if (name.equals(defaultClientName)) {
                defaultClientName = null;
            }
            return client;
        }
    }

    public Map<String, EzyClient> clear() {
        Map<String, EzyClient> removedClients;
        synchronized (clients) {
            removedClients = new HashMap<>(clients);
            this.clients.clear();
        }
        return removedClients;
    }

    public void disconnectClients() {
        List<EzyClient> clientsToDisconnect = new ArrayList<>();
        getClients(clientsToDisconnect);
        clientsToDisconnect.forEach(it ->
            processWithLogException(it::disconnect)
        );
    }

    public void startProcessEvents() {
        startProcessEvents(PROCESS_EVENTS_PERIOD);
    }

    public void startProcessEvents(int sleepTime) {
        synchronized (this) {
            if (processEventsScheduledExecutorService != null
                || processEventsEventLoopGroup != null) {
                return;
            }
            processEventsScheduledExecutorService = EzyExecutors
                .newSingleThreadScheduledExecutor(
                    "process-events"
                );
            startProcessEvents(
                processEventsScheduledExecutorService,
                sleepTime
            );
        }
    }

    public void startProcessEvents(
        ScheduledExecutorService scheduledExecutorService,
        int sleepTime
    ) {
        List<EzyClient> cachedClients = new ArrayList<>();
        scheduledExecutorService.scheduleAtFixedRate(
            () -> {
                try {
                    getClients(cachedClients);
                    for (EzyClient one : cachedClients) {
                        one.processEvents();
                    }
                } catch (Throwable e) {
                    logger.info("process events error", e);
                }
            },
            sleepTime,
            sleepTime,
            TimeUnit.MILLISECONDS
        );
    }

    public void startProcessEvents(
        EzyEventLoopGroup eventLoopGroup
    ) {
        startProcessEvents(eventLoopGroup, PROCESS_EVENTS_PERIOD);
    }

    public void startProcessEvents(
        EzyEventLoopGroup eventLoopGroup,
        int sleepTime
    ) {
        synchronized (this) {
            if (processEventsScheduledExecutorService != null
                || processEventsEventLoopGroup != null) {
                return;
            }
            List<EzyClient> cachedClients = new ArrayList<>();
            EzyEventLoopEvent event = () -> {
                try {
                    getClients(cachedClients);
                    for (EzyClient one : cachedClients) {
                        one.processEvents();
                    }
                } catch (Throwable e) {
                    logger.info("process events error", e);
                }
                return true;
            };
            eventLoopGroup.addScheduleEvent(event, sleepTime, sleepTime);
            this.processEventsEventLoopGroup = eventLoopGroup;
            this.processEventsLoopEvent = event;
        }
    }

    public void stopProcessEvents() {
        synchronized (this) {
            if (processEventsScheduledExecutorService != null) {
                processEventsScheduledExecutorService.shutdown();
                processEventsScheduledExecutorService = null;
            }
            if (processEventsEventLoopGroup != null) {
                processEventsEventLoopGroup.removeEvent(processEventsLoopEvent);
                processEventsEventLoopGroup = null;
                processEventsLoopEvent = null;
            }
        }
    }
}
