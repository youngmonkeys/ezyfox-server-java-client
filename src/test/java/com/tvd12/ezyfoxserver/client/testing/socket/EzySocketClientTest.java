package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.config.EzyReconnectConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionFailedReason;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.event.EzyDisconnectionEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandlers;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPingManager;
import com.tvd12.ezyfoxserver.client.socket.EzyPackage;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.socket.EzyResponseApi;
import com.tvd12.ezyfoxserver.client.socket.EzySocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzySocketReader;
import com.tvd12.ezyfoxserver.client.socket.EzySocketWriter;
import com.tvd12.ezyfoxserver.client.util.EzyValueStack;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void connectWithEvenLoopGroupTest() {
        // given
        TestEzySocketClient instance = new TestEzySocketClient();

        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        instance.setEventLoopGroup(eventLoopGroup);

        String host = "host";
        int port = 0;

        // when
        instance.connectTo(host, port);

        // then
        verify(eventLoopGroup, times(1)).addOneTimeEvent(
            any(Runnable.class),
            any(long.class)
        );
    }

    @Test
    public void connectToWaitMinSleepTimeBeforeReconnect() {
        // given
        TestEzySocketClient instance = new TestEzySocketClient();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        instance.setEventLoopGroup(eventLoopGroup);
        FieldUtil.setFieldValue(instance, "connectTime", System.currentTimeMillis());

        // when
        instance.connectTo("host", 0);

        // then
        ArgumentCaptor<Long> sleepTimeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(eventLoopGroup, times(1)).addOneTimeEvent(
            any(Runnable.class),
            sleepTimeCaptor.capture()
        );
        long sleepTime = sleepTimeCaptor.getValue();
        Asserts.assertTrue(sleepTime > 0 && sleepTime <= 2000);
    }

    @Test
    public void connectWithThreadAndConnectNowError() throws Exception {
        // given
        CountDownLatch connectLatch = new CountDownLatch(1);
        TestEzySocketClient instance = new TestEzySocketClient() {
            @Override
            protected boolean connectNow() {
                connectLatch.countDown();
                throw new IllegalStateException("test");
            }

            @Override
            protected int minSleepTimeBeforeReconnect() {
                return 0;
            }
        };

        // when
        instance.connectTo("host", 0);

        // then
        Asserts.assertTrue(connectLatch.await(3, TimeUnit.SECONDS));
        Thread.sleep(100);
    }

    @Test
    public void connectWithThreadSuccess() throws Exception {
        // given
        CountDownLatch startAdaptersLatch = new CountDownLatch(1);
        TestEzySocketClient instance = new TestEzySocketClient() {
            @Override
            protected boolean connectNow() {
                return true;
            }

            @Override
            protected void startAdapters() {
                startAdaptersLatch.countDown();
            }

            @Override
            protected int minSleepTimeBeforeReconnect() {
                return 0;
            }
        };

        // when
        instance.connectTo("host", 0);

        // then
        Asserts.assertTrue(startAdaptersLatch.await(3, TimeUnit.SECONDS));
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(instance, "socketStatuses");
        long startTime = System.currentTimeMillis();
        while (socketStatuses.last() != EzySocketStatus.CONNECTED
            && System.currentTimeMillis() - startTime < 3000) {
            Thread.sleep(10);
        }
        Asserts.assertEquals(socketStatuses.last(), EzySocketStatus.CONNECTED);
    }

    @Test
    public void reconnectReachMaxReconnectCount() {
        // given
        EzyReconnectConfig reconnectConfig = new EzyReconnectConfig.Builder(null)
            .maxReconnectCount(0)
            .build();
        TestEzySocketClient instance = new TestEzySocketClient();
        instance.setReconnectConfig(reconnectConfig);
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(instance, "socketStatuses");
        socketStatuses.push(EzySocketStatus.DISCONNECTED);

        // when
        boolean actual = instance.reconnect();

        // then
        Asserts.assertFalse(actual);
        Asserts.assertEquals(socketStatuses.last(), EzySocketStatus.DISCONNECTED);
    }

    @Test
    public void processConnectionExceptionTest() {
        // given
        TestEzySocketClient instance = new TestEzySocketClient();

        // when
        // then
        instance.processException(new ConnectException("Network is unreachable"));
        Asserts.assertEquals(
            instance.getConnectionFailedReason(),
            EzyConnectionFailedReason.NETWORK_UNREACHABLE
        );

        instance.processException(new ConnectException("Connection refused"));
        Asserts.assertEquals(
            instance.getConnectionFailedReason(),
            EzyConnectionFailedReason.CONNECTION_REFUSED
        );

        instance.processException(new UnknownHostException("test"));
        Asserts.assertEquals(
            instance.getConnectionFailedReason(),
            EzyConnectionFailedReason.UNKNOWN_HOST
        );

        instance.processException(new IllegalStateException("test"));
        Asserts.assertEquals(
            instance.getConnectionFailedReason(),
            EzyConnectionFailedReason.UNKNOWN
        );
    }

    @Test
    public void sendMessageError() throws Exception {
        // given
        TestEzySocketClient instance = new TestEzySocketClient();
        EzyResponseApi responseApi = mock(EzyResponseApi.class);
        doThrow(new IllegalStateException("test"))
            .when(responseApi).response(any(EzyPackage.class));
        FieldUtil.setFieldValue(instance, "responseApi", responseApi);
        EzyArray message = EzyEntityArrays.newArray("test");

        // when
        instance.sendMessage(message, false);

        // then
        verify(responseApi, times(1)).response(any(EzyPackage.class));
    }

    @Test
    public void processReceivedDisconnectCommandAndReaderStopped() {
        // given
        int reason = EzyDisconnectReason.ANOTHER_SESSION_LOGIN.getId();
        DisconnectContext context = new DisconnectContext(reason);
        when(context.socketReader.isStopped()).thenReturn(true);

        // when
        context.sut.processEventMessages();

        // then
        context.verifyDisconnected(reason);
    }

    @Test
    public void processReceivedDisconnectCommandAndWriterStopped() {
        // given
        int reason = EzyDisconnectReason.ADMIN_KICK.getId();
        DisconnectContext context = new DisconnectContext(reason);
        when(context.socketWriter.isStopped()).thenReturn(true);

        // when
        context.sut.processEventMessages();

        // then
        context.verifyDisconnected(reason);
    }

    @Test
    public void processReceivedDisconnectCommandAndAdaptersRunning() {
        // given
        int reason = EzyDisconnectReason.ADMIN_KICK.getId();
        DisconnectContext context = new DisconnectContext(reason);

        // when
        context.sut.processEventMessages();

        // then
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(context.sut, "socketStatuses");
        Asserts.assertEquals(socketStatuses.last(), EzySocketStatus.DISCONNECTING);
        Asserts.assertEquals(FieldUtil.getFieldValue(context.sut, "disconnectReason"), reason);
        verify(context.pingSchedule, times(0)).stop();
        verify(context.dataHandlers, times(0)).handle(any(EzyCommand.class), any(EzyArray.class));
    }

    @Test
    public void processReceivedDisconnectCommandWithoutData() {
        // given
        DisconnectContext context = new DisconnectContext(null);

        // when
        context.sut.processEventMessages();

        // then
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(context.sut, "socketStatuses");
        Asserts.assertEquals(socketStatuses.last(), EzySocketStatus.DISCONNECTING);
        Asserts.assertEquals(
            FieldUtil.getFieldValue(context.sut, "disconnectReason"),
            EzyDisconnectReason.UNKNOWN.getId()
        );
    }

    @Test
    public void processReceivedDisconnectCommandWithEmptyData() {
        // given
        DisconnectContext context = new DisconnectContext(EzyEntityArrays.newArray());

        // when
        context.sut.processEventMessages();

        // then
        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(context.sut, "socketStatuses");
        Asserts.assertEquals(socketStatuses.last(), EzySocketStatus.DISCONNECTING);
        Asserts.assertEquals(
            FieldUtil.getFieldValue(context.sut, "disconnectReason"),
            EzyDisconnectReason.UNKNOWN.getId()
        );
    }

    @Test
    public void processReceivedDisconnectCommandWithIgnoredLogCommand() {
        // given
        int reason = EzyDisconnectReason.ADMIN_KICK.getId();
        DisconnectContext context = new DisconnectContext(reason);
        context.sut.setIgnoredLogCommands(Collections.singleton(EzyCommand.DISCONNECT));

        // when
        context.sut.processEventMessages();

        // then
        Asserts.assertEquals(FieldUtil.getFieldValue(context.sut, "disconnectReason"), reason);
    }

    @Test
    public void processReceivedMessagesWhenNoMessage() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        EzyDataHandlers dataHandlers = mock(EzyDataHandlers.class);
        when(handlerManager.getEventHandlers()).thenReturn(mock(EzyEventHandlers.class));
        when(handlerManager.getDataHandlers()).thenReturn(dataHandlers);
        EzyPingManager pingManager = mock(EzyPingManager.class);

        TestEzySocketClient sut = new TestEzySocketClient();
        sut.setHandlerManager(handlerManager);
        sut.setPingManager(pingManager);
        sut.setPingSchedule(mock(EzyPingSchedule.class));

        EzySocketReader socketReader = mock(EzySocketReader.class);
        when(socketReader.isActive()).thenReturn(true);
        FieldUtil.setFieldValue(sut, "socketReader", socketReader);
        FieldUtil.setFieldValue(sut, "socketWriter", mock(EzySocketWriter.class));

        EzyValueStack<EzySocketStatus> socketStatuses =
            FieldUtil.getFieldValue(sut, "socketStatuses");
        socketStatuses.push(EzySocketStatus.CONNECTED);

        // when
        sut.processEventMessages();

        // then
        verify(socketReader, times(1)).popMessages(any());
        verify(pingManager, never()).setLostPingCount(anyInt());
        verify(dataHandlers, never()).handle(any(EzyCommand.class), any(EzyArray.class));
        Asserts.assertEquals(socketStatuses.last(), EzySocketStatus.CONNECTED);
    }

    private static class DisconnectContext {
        private final TestEzySocketClient sut;
        private final EzySocketReader socketReader;
        private final EzySocketWriter socketWriter;
        private final EzyPingSchedule pingSchedule;
        private final EzyEventHandlers eventHandlers;
        private final EzyDataHandlers dataHandlers;

        private DisconnectContext(int reason) {
            this(EzyEntityArrays.newArray(reason));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private DisconnectContext(EzyArray data) {
            EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
            eventHandlers = mock(EzyEventHandlers.class);
            dataHandlers = mock(EzyDataHandlers.class);
            when(handlerManager.getEventHandlers()).thenReturn(eventHandlers);
            when(handlerManager.getDataHandlers()).thenReturn(dataHandlers);

            sut = new TestEzySocketClient();
            sut.setHandlerManager(handlerManager);
            sut.setPingManager(mock(EzyPingManager.class));
            sut.setIgnoredLogCommands(Collections.emptySet());
            pingSchedule = mock(EzyPingSchedule.class);
            sut.setPingSchedule(pingSchedule);

            socketReader = mock(EzySocketReader.class);
            when(socketReader.isActive()).thenReturn(true);
            doAnswer(invocation -> {
                List localMessageQueue = invocation.getArgumentAt(0, List.class);
                EzyArray message = data != null
                    ? EzyEntityArrays.newArray(EzyCommand.DISCONNECT.getId(), data)
                    : EzyEntityArrays.newArray(EzyCommand.DISCONNECT.getId());
                localMessageQueue.add(message);
                return null;
            })
                .when(socketReader).popMessages(any());
            FieldUtil.setFieldValue(sut, "socketReader", socketReader);

            socketWriter = mock(EzySocketWriter.class);
            FieldUtil.setFieldValue(sut, "socketWriter", socketWriter);

            EzyValueStack<EzySocketStatus> socketStatuses =
                FieldUtil.getFieldValue(sut, "socketStatuses");
            socketStatuses.push(EzySocketStatus.CONNECTED);
        }

        private void verifyDisconnected(int reason) {
            EzyValueStack<EzySocketStatus> socketStatuses =
                FieldUtil.getFieldValue(sut, "socketStatuses");
            Asserts.assertEquals(socketStatuses.last(), EzySocketStatus.DISCONNECTED);
            verify(pingSchedule, times(1)).stop();
            verify(socketReader, times(1)).stop();
            verify(socketWriter, times(1)).stop();
            Asserts.assertNull(FieldUtil.getFieldValue(sut, "socketReader"));
            Asserts.assertNull(FieldUtil.getFieldValue(sut, "socketWriter"));
            verify(dataHandlers, times(0)).handle(any(EzyCommand.class), any(EzyArray.class));
            ArgumentCaptor<EzyEvent> eventCaptor = ArgumentCaptor.forClass(EzyEvent.class);
            verify(eventHandlers, times(1)).handle(eventCaptor.capture());
            EzyDisconnectionEvent event = (EzyDisconnectionEvent) eventCaptor.getValue();
            Asserts.assertEquals(event.getReason(), reason);
        }
    }

    public static class TestEzySocketClient extends EzySocketClient {

        private void processException(Throwable e) {
            processConnectionException(e);
        }

        private EzyConnectionFailedReason getConnectionFailedReason() {
            return connectionFailedReason;
        }

        @Override
        protected boolean connectNow() {
            return false;
        }

        @Override
        protected void createAdapters() {
            socketReader = mock(EzySocketReader.class);
            socketWriter = mock(EzySocketWriter.class);
        }

        @Override
        protected void startAdapters() {
        }

        @Override
        protected void resetSocket() {
        }

        @Override
        protected void closeSocket() {
        }
    }
}
