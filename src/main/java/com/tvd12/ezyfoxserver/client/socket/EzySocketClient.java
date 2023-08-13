package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.client.codec.EzyCodecFactory;
import com.tvd12.ezyfoxserver.client.codec.EzySimpleCodecFactory;
import com.tvd12.ezyfoxserver.client.config.EzyReconnectConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.*;
import com.tvd12.ezyfoxserver.client.event.*;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandlers;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.util.EzyValueStack;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.tvd12.ezyfoxserver.client.constant.EzySocketStatuses.*;

public abstract class EzySocketClient
    extends EzyLoggable
    implements EzyISocketClient, EzySocketDelegate {

    protected String host;
    protected int port;
    protected int reconnectCount;
    protected long connectTime;
    protected int disconnectReason;
    protected long sessionId;
    protected String sessionToken;
    protected byte[] sessionKey;
    protected EzyReconnectConfig reconnectConfig;
    protected EzyHandlerManager handlerManager;
    protected Set<Object> ignoredLogCommands;
    protected EzyPingManager pingManager;
    protected EzyPingSchedule pingSchedule;
    protected EzyEventHandlers eventHandlers;
    protected EzyDataHandlers dataHandlers;
    protected EzySocketReader socketReader;
    protected EzySocketWriter socketWriter;
    protected EzyConnectionFailedReason connectionFailedReason;
    protected final EzyCodecFactory codecFactory;
    protected final EzyPacketQueue packetQueue;
    protected final EzySocketEventQueue socketEventQueue;
    protected final EzyResponseApi responseApi;
    protected final List<EzyEvent> localEventQueue;
    protected final List<EzyArray> localMessageQueue;
    protected final List<EzySocketStatus> localSocketStatuses;
    protected final EzyValueStack<EzySocketStatus> socketStatuses;

    public EzySocketClient() {
        this(EzySocketClientConfig.DEFAULT);
    }

    public EzySocketClient(EzySocketClientConfig config) {
        this.codecFactory = new EzySimpleCodecFactory(
            config.isEnableEncryption()
        );
        this.packetQueue = new EzyBlockingPacketQueue();
        this.socketEventQueue = new EzySocketEventQueue();
        this.responseApi = newResponseApi();
        this.localEventQueue = new ArrayList<>();
        this.localMessageQueue = new ArrayList<>();
        this.localSocketStatuses = new ArrayList<>();
        this.socketStatuses = new EzyValueStack<>(EzySocketStatus.NOT_CONNECT);
    }

    private EzyResponseApi newResponseApi() {
        Object encoder = codecFactory.newEncoder(EzyConnectionType.SOCKET);
        EzySocketDataEncoder socketDataEncoder = new EzySimpleSocketDataEncoder(encoder);
        return new EzySocketResponseApi(socketDataEncoder, packetQueue);
    }

    @Override
    public void connectTo(String host, int port) {
        EzySocketStatus status = socketStatuses.last();
        if (!isSocketConnectable(status)) {
            logger.info("socket is connecting...");
            return;
        }
        this.socketStatuses.push(EzySocketStatus.CONNECTING);
        this.host = host;
        this.port = port;
        this.reconnectCount = 0;
        this.connect0(0);
    }

    @Override
    public boolean reconnect() {
        EzySocketStatus status = socketStatuses.last();
        if (!isSocketReconnectable(status)) {
            logger.info("socket is not in a reconnectable status");
            return false;
        }
        int maxReconnectCount = reconnectConfig.getMaxReconnectCount();
        if (reconnectCount >= maxReconnectCount) {
            return false;
        }
        socketStatuses.push(EzySocketStatus.RECONNECTING);
        int reconnectSleepTime = reconnectConfig.getReconnectPeriod();
        connect0(reconnectSleepTime);
        reconnectCount++;
        logger.info("try reconnect to server: " + reconnectCount + ", waiting time: " + reconnectSleepTime);
        EzyEvent tryConnectEvent = new EzyTryConnectEvent(reconnectCount);
        socketEventQueue.addEvent(tryConnectEvent);
        return true;
    }

    protected void connect0(final int sleepTime) {
        clearAdapters();
        createAdapters();
        updateAdapters();
        closeSocket();
        packetQueue.clear();
        socketEventQueue.clear();
        socketStatuses.clear();
        disconnectReason = EzyDisconnectReason.UNKNOWN.getId();
        connectionFailedReason = EzyConnectionFailedReason.UNKNOWN;
        Thread newThread = new Thread(() -> connect1(sleepTime));
        newThread.setName("ezyfox-connection");
        newThread.start();
    }

    protected void connect1(int sleepTime) {
        long currentTime = System.currentTimeMillis();
        long dt = currentTime - connectTime;
        long realSleepTime = sleepTime;
        if (sleepTime <= 0) {
            int minSleepTimeBeforeReconnect = minSleepTimeBeforeReconnect();
            if (dt < minSleepTimeBeforeReconnect) {
                realSleepTime = minSleepTimeBeforeReconnect - dt;
            }
        }
        if (realSleepTime >= 0) {
            sleepBeforeConnect(realSleepTime);
        }
        socketStatuses.push(EzySocketStatus.CONNECTING);
        boolean success = this.connectNow();
        connectTime = System.currentTimeMillis();

        if (success) {
            this.reconnectCount = 0;
            this.startAdapters();
            this.socketStatuses.push(EzySocketStatus.CONNECTED);
        } else {
            this.resetSocket();
            this.socketStatuses.push(EzySocketStatus.CONNECT_FAILED);
        }
    }

    protected int minSleepTimeBeforeReconnect() {
        return 2000;
    }

    protected void sleepBeforeConnect(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract boolean connectNow();

    protected abstract void createAdapters();

    protected void updateAdapters() {
        Object decoder = codecFactory.newDecoder(EzyConnectionType.SOCKET);
        EzySocketDataDecoder socketDataDecoder = new EzySimpleSocketDataDecoder(decoder);
        socketReader.setDecoder(socketDataDecoder);
        socketWriter.setPacketQueue(packetQueue);
    }

    protected abstract void startAdapters();

    protected void clearAdapters() {
        clearAdapter(socketReader);
        clearAdapter(socketWriter);
        socketReader = null;
        socketWriter = null;
    }

    protected void clearAdapter(EzySocketAdapter adapter) {
        if (adapter != null) {
            adapter.stop();
        }
    }

    protected void clearComponents(int disconnectReason) {}

    protected abstract void resetSocket();

    protected abstract void closeSocket();

    public void onDisconnected(int reason) {
        pingSchedule.stop();
        packetQueue.clear();
        packetQueue.wakeup();
        socketEventQueue.clear();
        closeSocket();
        clearAdapters();
        clearComponents(reason);
        socketStatuses.push(EzySocketStatus.DISCONNECTED);
    }

    @Override
    public void disconnect(int reason) {
        if (socketStatuses.last() != EzySocketStatus.CONNECTED) {
            return;
        }
        onDisconnected(disconnectReason = reason);
    }

    @Override
    public void close() {
        disconnect(EzyDisconnectReason.CLOSE.getId());
        pingSchedule.shutdown();
    }

    @Override
    public void sendMessage(EzyArray message, boolean encrypted) {
        EzyPackage pack = new EzySimplePackage(
            message,
            encrypted,
            sessionKey,
            EzyTransportType.TCP
        );
        try {
            responseApi.response(pack);
        } catch (Throwable e) {
            logger.info("send message: " + message + " error", e);
        }
    }

    public void processEventMessages() {
        processReceivedMessages();
        processStatuses();
        processEvents();
    }

    protected void processStatuses() {
        socketStatuses.popAll(localSocketStatuses);
        for (EzySocketStatus status : localSocketStatuses) {
            if (status == EzySocketStatus.CONNECTED) {
                EzyEvent evt = new EzyConnectionSuccessEvent();
                socketEventQueue.addEvent(evt);
            } else if (status == EzySocketStatus.CONNECT_FAILED) {
                EzyEvent evt = new EzyConnectionFailureEvent(connectionFailedReason);
                socketEventQueue.addEvent(evt);
                break;
            } else if (status == EzySocketStatus.DISCONNECTED) {
                EzyEvent evt = new EzyDisconnectionEvent(disconnectReason);
                socketEventQueue.addEvent(evt);
                break;
            }
        }
        localSocketStatuses.clear();
    }

    protected void processEvents() {
        socketEventQueue.popAll(localEventQueue);
        try {
            for (EzyEvent evt : localEventQueue) {
                eventHandlers.handle(evt);
            }
        } finally {
            localEventQueue.clear();
        }
    }

    protected void processReceivedMessages() {
        EzySocketStatus status = socketStatuses.last();
        if (status == EzySocketStatus.CONNECTED) {
            if (socketReader.isActive()) {
                processReceivedMessages0();
            }
        }
        EzySocketStatus statusLast = socketStatuses.last();
        if (isSocketDisconnectable(statusLast)) {
            if (socketReader.isStopped()) {
                onDisconnected(disconnectReason);
            } else if (socketWriter.isStopped()) {
                onDisconnected(disconnectReason);
            }
        }
    }

    protected void processReceivedMessages0() {
        popReadMessages();
        try {
            if (localMessageQueue.size() > 0) {
                pingManager.setLostPingCount(0);
            }
            for (EzyArray ezyArray : localMessageQueue) {
                processReceivedMessage(ezyArray);
            }
        } finally {
            localMessageQueue.clear();
        }
    }

    protected void popReadMessages() {
        socketReader.popMessages(localMessageQueue);
    }

    protected void processReceivedMessage(EzyArray message) {
        int cmdId = message.get(0, int.class);
        EzyArray data = message.get(1, EzyArray.class, null);
        EzyCommand cmd = EzyCommand.valueOf(cmdId);
        printReceivedData(cmd, data);
        if (cmd == EzyCommand.DISCONNECT) {
            disconnectReason = data.get(0, int.class);
            socketStatuses.push(EzySocketStatus.DISCONNECTING);
        } else {
            dataHandlers.handle(cmd, data);
        }
    }

    protected void processConnectionException(Throwable e) {
        if (e instanceof ConnectException) {
            ConnectException c = (ConnectException) e;
            if ("Network is unreachable".equalsIgnoreCase(c.getMessage())) {
                connectionFailedReason = EzyConnectionFailedReason.NETWORK_UNREACHABLE;
            } else {
                connectionFailedReason = EzyConnectionFailedReason.CONNECTION_REFUSED;
            }
        } else if (e instanceof UnknownHostException) {
            connectionFailedReason = EzyConnectionFailedReason.UNKNOWN_HOST;
        } else {
            connectionFailedReason = EzyConnectionFailedReason.UNKNOWN;
        }
    }

    protected void printReceivedData(EzyCommand cmd, EzyArray data) {
        if (!ignoredLogCommands.contains(cmd)) {
            logger.debug("received command: " + cmd + " and data: " + data);
        }
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
        this.socketReader.setSessionKey(sessionKey);
    }

    public void setPingManager(EzyPingManager pingManager) {
        this.pingManager = pingManager;
    }

    public void setPingSchedule(EzyPingSchedule pingSchedule) {
        this.pingSchedule = pingSchedule;
        this.pingSchedule.setSocketEventQueue(socketEventQueue);
    }

    public void setHandlerManager(EzyHandlerManager handlerManager) {
        this.handlerManager = handlerManager;
        this.dataHandlers = handlerManager.getDataHandlers();
        this.eventHandlers = handlerManager.getEventHandlers();
    }

    public void setReconnectConfig(EzyReconnectConfig reconnectConfig) {
        this.reconnectConfig = reconnectConfig;
    }

    public void setIgnoredLogCommands(Set<Object> ignoredLogCommands) {
        this.ignoredLogCommands = ignoredLogCommands;
    }
}
