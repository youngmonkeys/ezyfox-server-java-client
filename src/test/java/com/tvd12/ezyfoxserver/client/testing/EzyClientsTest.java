package com.tvd12.ezyfoxserver.client.testing;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClients;
import com.tvd12.ezyfoxserver.client.EzyTcpClient;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class EzyClientsTest {

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
}
