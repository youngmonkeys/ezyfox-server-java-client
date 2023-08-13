package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.config.EzyReconnectConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandlers;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzySocketReader;
import com.tvd12.ezyfoxserver.client.socket.EzySocketWriter;
import com.tvd12.ezyfoxserver.client.util.EzyValueStack;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EzySocketClientTest extends BaseTest {

    @Test
    public void connectToInConnectingStatus() {
        // given
        String host = "host";
        int port = 0;
        EzySocketClient sut = spy(EzySocketClient.class);
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.CONNECTING);

        // when
        // then
        sut.connectTo(host, port);
    }

    @Test
    public void reconnectInReconnectingStatus() {
        // given
        EzySocketClient sut = spy(EzySocketClient.class);
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.RECONNECTING);

        // when
        // then
        sut.reconnect();
    }

    @Test
    public void reconnect() {
        // given
        EzyReconnectConfig reconnectConfig = new EzyReconnectConfig.Builder(null)
            .maxReconnectCount(100)
            .build();
        EzySocketReader mockSocketReader = mock(EzySocketReader.class);
        EzySocketWriter mockSocketWriter = mock(EzySocketWriter.class);
        EzySocketClientConfig config = mock(EzySocketClientConfig.class);
        EzySocketClient sut = new EzySocketClient(config) {

            @Override
            protected void startAdapters() {}

            @Override
            protected void resetSocket() {}

            @Override
            protected void createAdapters() {
                this.socketReader = mockSocketReader;
                this.socketWriter = mockSocketWriter;
            }

            @Override
            protected boolean connectNow() {
                return false;
            }

            @Override
            protected void closeSocket() {}
        };
        sut.setReconnectConfig(reconnectConfig);
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.DISCONNECTED);

        // when
        // then
        sut.reconnect();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void processEventMessagesStatusConnected() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        EzyEventHandlers eventHandlers = mock(EzyEventHandlers.class);
        when(handlerManager.getEventHandlers()).thenReturn(eventHandlers);

        EzyEventHandler eventHandler = mock(EzyEventHandler.class);
        when(eventHandlers.getHandler(EzyEventType.CONNECTION_SUCCESS))
            .thenReturn(eventHandler);

        EzySocketClient sut = spy(EzySocketClient.class);
        sut.setHandlerManager(handlerManager);

        EzySocketReader mockSocketReader = mock(EzySocketReader.class);
        FieldUtil.setFieldValue(sut, "socketReader", mockSocketReader);

        EzySocketWriter mockSocketWriter = mock(EzySocketWriter.class);
        FieldUtil.setFieldValue(sut, "socketWriter", mockSocketWriter);

        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.CONNECTED);

        // when
        // then
        sut.processEventMessages();
    }

    @Test
    public void processEventMessagesStatusConnectFailed() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        EzyEventHandlers eventHandlers = mock(EzyEventHandlers.class);
        when(handlerManager.getEventHandlers()).thenReturn(eventHandlers);

        EzySocketClient sut = spy(EzySocketClient.class);
        sut.setHandlerManager(handlerManager);

        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.CONNECT_FAILED);

        // when
        // then
        sut.processEventMessages();
    }

    @Test
    public void processEventMessagesStatusDisconnected() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        EzyEventHandlers eventHandlers = mock(EzyEventHandlers.class);
        when(handlerManager.getEventHandlers()).thenReturn(eventHandlers);

        EzySocketClient sut = spy(EzySocketClient.class);
        sut.setHandlerManager(handlerManager);

        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.DISCONNECTED);

        // when
        // then
        sut.processEventMessages();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void processEventMessagesReceivedMessage() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        EzyEventHandlers eventHandlers = mock(EzyEventHandlers.class);
        when(handlerManager.getEventHandlers()).thenReturn(eventHandlers);

        EzyEventHandler eventHandler = mock(EzyEventHandler.class);
        when(eventHandlers.getHandler(EzyEventType.CONNECTION_SUCCESS))
            .thenReturn(eventHandler);

        EzyDataHandlers dataHandlers = mock(EzyDataHandlers.class);
        when(handlerManager.getDataHandlers()).thenReturn(dataHandlers);

        EzySocketClient sut = spy(EzySocketClient.class);
        sut.setHandlerManager(handlerManager);

        EzyPingManager pingManager = mock(EzyPingManager.class);
        sut.setPingManager(pingManager);

        sut.setIgnoredLogCommands(Collections.emptySet());

        EzySocketReader mockSocketReader = mock(EzySocketReader.class);
        when(mockSocketReader.isActive()).thenReturn(true);
        FieldUtil.setFieldValue(sut, "socketReader", mockSocketReader);

        doAnswer(invocation -> {
            List localMessageQueue = invocation.getArgumentAt(0, List.class);
            EzyCommand cmd = EzyCommand.HANDSHAKE;
            EzyArray data = EzyEntityArrays.newArray("test");
            EzyArray message = EzyEntityArrays.newArray(cmd.getId(), data);
            localMessageQueue.add(message);
            return null;
        })
            .when(mockSocketReader).popMessages(any());

        EzySocketWriter mockSocketWriter = mock(EzySocketWriter.class);
        FieldUtil.setFieldValue(sut, "socketWriter", mockSocketWriter);

        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.CONNECTED);

        // when
        // then
        sut.processEventMessages();
    }

    @Test
    public void disconnectWhenConnected() {
        // given
        EzySocketClient sut = spy(EzySocketClient.class);

        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        sut.setPingSchedule(pingSchedule);

        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.CONNECTED);

        // when
        // then
        sut.disconnect(EzyDisconnectReason.CLOSE.getId());
    }
}
