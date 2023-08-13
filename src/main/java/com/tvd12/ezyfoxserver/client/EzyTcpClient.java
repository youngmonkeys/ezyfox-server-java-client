package com.tvd12.ezyfoxserver.client;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.entity.EzyEntity;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.constant.EzySslType;
import com.tvd12.ezyfoxserver.client.entity.*;
import com.tvd12.ezyfoxserver.client.manager.*;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequestSerializer;
import com.tvd12.ezyfoxserver.client.request.EzySimpleRequestSerializer;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;
import com.tvd12.ezyfoxserver.client.setup.EzySimpleSetup;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSslSocketClient;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.HashSet;
import java.util.Set;

import static com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatuses.isClientConnectable;
import static com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatuses.isClientReconnectable;

public class EzyTcpClient
    extends EzyEntity
    implements EzyClient, EzyMeAware, EzyZoneAware {

    @Setter
    @Getter
    protected EzyUser me;
    @Setter
    @Getter
    protected EzyZone zone;
    @Getter
    protected long sessionId;
    @Setter
    @Getter
    protected byte[] publicKey;
    @Setter
    @Getter
    protected byte[] privateKey;
    @Getter
    protected byte[] sessionKey;
    @Getter
    protected String sessionToken;
    @Setter
    @Getter
    protected EzyConnectionStatus status;
    @Setter
    @Getter
    protected EzyConnectionStatus udpStatus;
    @Getter
    protected final String name;
    protected final EzySetup settingUp;
    @Getter
    protected final EzyClientConfig config;
    @Getter
    protected final EzyPingManager pingManager;
    @Getter
    protected final EzyHandlerManager handlerManager;
    protected final EzyRequestSerializer requestSerializer;
    protected final Set<Object> ignoredLogCommands;
    protected final EzySocketClient socketClient;
    @Getter
    protected final EzyPingSchedule pingSchedule;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public EzyTcpClient(EzyClientConfig config) {
        this.config = config;
        this.name = config.getClientName();
        this.status = EzyConnectionStatus.NULL;
        this.pingManager = new EzySimplePingManager(config.getPing());
        this.pingSchedule = new EzyPingSchedule(this);
        this.handlerManager = new EzySimpleHandlerManager(this);
        this.requestSerializer = new EzySimpleRequestSerializer();
        this.settingUp = new EzySimpleSetup(handlerManager);
        this.ignoredLogCommands = newIgnoredLogCommands();
        this.socketClient = newSocketClient();
    }

    protected Set<Object> newIgnoredLogCommands() {
        Set<Object> set = new HashSet<>();
        set.add(EzyCommand.PING);
        set.add(EzyCommand.PONG);
        return set;
    }

    protected EzySocketClient newSocketClient() {
        EzySocketClient client = newTcpSocketClient(config);
        client.setPingSchedule(pingSchedule);
        client.setPingManager(pingManager);
        client.setHandlerManager(handlerManager);
        client.setReconnectConfig(config.getReconnect());
        client.setIgnoredLogCommands(ignoredLogCommands);
        return client;
    }

    protected EzySocketClient newTcpSocketClient(EzySocketClientConfig config) {
        return isEnableCertificationSSL()
            ? new EzyTcpSslSocketClient(config)
            : new EzyTcpSocketClient(config);
    }

    @Override
    public EzySetup setup() {
        return settingUp;
    }

    @Override
    public void connect(String host, int port) {
        try {
            if (!isClientConnectable(status)) {
                logger.info("client has already connected to: " + host + ":" + port);
                return;
            }
            preConnect();
            socketClient.connectTo(host, port);
            setStatus(EzyConnectionStatus.CONNECTING);
        } catch (Exception e) {
            logger.error("connect to server error", e);
        }
    }

    public boolean reconnect() {
        if (!isClientReconnectable(status)) {
            String host = socketClient.getHost();
            int port = socketClient.getPort();
            logger.info("client has already connected to: " + host + ":" + port);
            return false;
        }
        preConnect();
        boolean success = socketClient.reconnect();
        if (success) {
            setStatus(EzyConnectionStatus.RECONNECTING);
        }
        return success;
    }

    protected void preConnect() {
        this.me = null;
        this.zone = null;
        this.publicKey = null;
        this.privateKey = null;
        this.sessionKey = null;
    }

    @Override
    public void disconnect(int reason) {
        socketClient.disconnect(reason);
    }

    @Override
    public void send(EzyRequest request, boolean encrypted) {
        Object cmd = request.getCommand();
        EzyData data = request.serialize();
        send((EzyCommand) cmd, (EzyArray) data, encrypted);
    }

    @Override
    public void send(EzyCommand cmd, EzyArray data, boolean encrypted) {
        boolean shouldEncrypted = encrypted;
        if (encrypted && sessionKey == null) {
            if (config.isEnableDebug()) {
                shouldEncrypted = false;
            } else {
                throw new IllegalArgumentException(
                    "can not send command: " + cmd + " " +
                        "you must enable SSL or enable debug mode by configuration " +
                        "when you create the client"
                );
            }

        }
        EzyArray array = requestSerializer.serialize(cmd, data);
        socketClient.sendMessage(array, shouldEncrypted);
        printSentData(cmd, data);
    }

    @Override
    public void processEvents() {
        socketClient.processEventMessages();
    }

    @Override
    public boolean isEnableSSL() {
        return config.isEnableSSL();
    }

    @Override
    public EzySslType getSslType() {
        return config.getSslType();
    }

    @Override
    public boolean isEnableEncryption() {
        return config.isEnableEncryption();
    }

    @Override
    public boolean isEnableCertificationSSL() {
        return config.isEnableCertificationSSL();
    }

    @Override
    public void setSslContext(SSLContext sslContext) {
        if (socketClient instanceof EzyTcpSslSocketClient) {
            ((EzyTcpSslSocketClient) socketClient).setSslContext(sslContext);
        }
    }

    @Override
    public boolean isEnableDebug() {
        return config.isEnableDebug();
    }

    @Override
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
        this.socketClient.setSessionId(sessionId);
    }

    @Override
    public void setSessionToken(String token) {
        this.sessionToken = token;
        this.socketClient.setSessionToken(sessionToken);
    }

    @Override
    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
        this.socketClient.setSessionKey(sessionKey);
    }

    @Override
    public EzySocketClient getSocket() {
        return socketClient;
    }

    @Override
    public EzyApp getApp() {
        if (zone != null) {
            EzyAppManager appManager = zone.getAppManager();
            return appManager.getApp();
        }
        return null;
    }

    @Override
    public EzyApp getAppById(int appId) {
        if (zone != null) {
            EzyAppManager appManager = zone.getAppManager();
            return appManager.getAppById(appId);
        }
        return null;
    }

    protected void printSentData(EzyCommand cmd, EzyArray data) {
        if (!ignoredLogCommands.contains(cmd)) {
            logger.debug("send command: " + cmd + " and data: " + data);
        }
    }

    @Override
    public void udpConnect(int port) {
        throw new UnsupportedOperationException("only support TCP, use EzyUTClient instead");
    }

    @Override
    public void udpConnect(String host, int port) {
        throw new UnsupportedOperationException("only support TCP, use EzyUTClient instead");
    }

    @Override
    public void udpSend(EzyRequest request, boolean encrypted) {
        throw new UnsupportedOperationException("only support TCP, use EzyUTClient instead");
    }

    @Override
    public void udpSend(EzyCommand cmd, EzyArray data, boolean encrypted) {
        throw new UnsupportedOperationException("only support TCP, use EzyUTClient instead");
    }

    @Override
    public void close() {
        socketClient.close();
    }
}
