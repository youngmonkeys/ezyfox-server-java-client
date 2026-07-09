package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfox.concurrent.EzyEventLoopEvent;
import com.tvd12.ezyfox.concurrent.EzyEventLoopGroup;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;
import com.tvd12.ezyfoxserver.client.EzyTcpClient;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class EzyClientsTest extends BaseTest {

    @BeforeMethod
    public void before() {
        EzyClients.getInstance().clear();
    }

    @Test
    public void getDefaultClientReturnNull() {
        // given
        EzyClients sut = EzyClients.getInstance();

        // when
        EzyClient defaultClient = sut.getDefaultClient();

        // then
        assert defaultClient == null;
    }

    @Test
    public void newTcpClientFirstTime() {
        // given
        String clientName = "testClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyClients sut = EzyClients.getInstance();

        // when
        EzyClient client = sut.newClient(config);

        // then
        assert client.getClass() == EzyTcpClient.class;
    }

    @Test
    public void newTcpClientExisted() {
        // given
        String zoneName = "testZoneName";
        EzyClientConfig config = EzyClientConfig.builder()
            .zoneName(zoneName)
            .build();
        EzyClients sut = EzyClients.getInstance();

        // when
        EzyClient client1 = sut.newClient(config);
        EzyClient client2 = sut.newClient(config);

        // then
        assert client1 == client2;
    }

    @Test
    public void newUdpClient() {
        // given
        String clientName = "testUTClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyClients sut = EzyClients.getInstance();

        // when
        EzyClient client = sut.newClient(EzyTransportType.UDP, config);

        // then
        assert client.getClass() == EzyUTClient.class;
    }

    @Test
    public void newDefaultTcpClient() {
        // given
        String clientName = "testDefaultClientName";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyClients sut = EzyClients.getInstance();

        // when
        EzyClient client = sut.newDefaultClient(config);

        // then
        assert sut.getDefaultClient() == client;
    }

    @Test
    public void getClient() {
        // given
        String clientName = "testClientNameForGetClient";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyClient client = new EzyTcpClient(config);
        EzyClients sut = EzyClients.getInstance();
        sut.addClient(client);

        // when
        EzyClient actualClient = sut.getClient(clientName);
        assert actualClient == client;
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void getClientWithNullName() {
        // given
        EzyClients sut = EzyClients.getInstance();

        // when
        sut.getClient(null);
    }

    @Test
    public void getClients() {
        String clientName = "testClientNameForGetClients";
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyClient client = new EzyTcpClient(config);
        EzyClients sut = EzyClients.getInstance();
        sut.addClient(client);

        // when
        List<EzyClient> cachedClients = new ArrayList<>();
        sut.getClients(cachedClients);
        assert cachedClients.size() >= 1;
    }

    @Test
    public void addRemoveGetDefaultClient() {
        // given
        EzyClients clients = EzyClients.getInstance();
        String clientName = RandomUtil.randomShortAlphabetString();
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();

        // when
        EzyClient client = clients.newClient(config);

        // then
        Asserts.assertEquals(
            clients.getDefaultClient(),
            client
        );
        clients.disconnectClients();
        Asserts.assertEquals(clients.removeClient(clientName), client);
        client.close();
        Asserts.assertNull(clients.removeClient(clientName));
        Asserts.assertNull(clients.getDefaultClient());
    }

    @Test
    public void processEventTest() throws InterruptedException {
        // given
        EzyClients clients = EzyClients.getInstance();
        String clientName = RandomUtil.randomShortAlphabetString();
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyClient client = clients.newClient(config);
        clients.addClient(client);

        // when
        clients.startProcessEvents();
        Thread.sleep(100);
        clients.startProcessEvents();

        // then
        clients.stopProcessEvents();
    }

    @Test
    public void processEventsWithEventLoopGroupTest() {
        // given
        EzyClients clients = EzyClients.getInstance();
        String clientName = RandomUtil.randomShortAlphabetString();
        EzyClientConfig config = EzyClientConfig.builder()
            .clientName(clientName)
            .build();
        EzyClient client = clients.newClient(config);
        clients.addClient(client);
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);
        ArgumentCaptor<EzyEventLoopEvent> eventCaptor =
            ArgumentCaptor.forClass(EzyEventLoopEvent.class);

        // when
        clients.startProcessEvents(eventLoopGroup);
        // calling again while already started must be a no-op
        clients.startProcessEvents(eventLoopGroup);

        // then
        verify(eventLoopGroup, times(1))
            .addScheduleEvent(eventCaptor.capture(), anyLong(), anyLong());

        // the shared event loop thread would call this repeatedly
        EzyEventLoopEvent event = eventCaptor.getValue();
        assert event.call();

        clients.stopProcessEvents();
        verify(eventLoopGroup, times(1)).removeEvent(event);
    }

    @Test
    public void startProcessEventsIgnoredWhenEventLoopGroupAlreadyStarted() {
        // given
        EzyClients clients = EzyClients.getInstance();
        EzyEventLoopGroup eventLoopGroup = mock(EzyEventLoopGroup.class);

        // when
        clients.startProcessEvents(eventLoopGroup);
        clients.startProcessEvents(3);

        // then
        assert !isProcessEventsScheduledExecutorServiceRunning(clients);
        clients.stopProcessEvents();
    }

    private boolean isProcessEventsScheduledExecutorServiceRunning(
        EzyClients clients
    ) {
        return FieldUtil.getFieldValue(
            clients,
            "processEventsScheduledExecutorService"
        ) != null;
    }
}
