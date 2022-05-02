package com.tvd12.ezyfoxserver.client.testing.entity;

import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;
import com.tvd12.ezyfoxserver.client.entity.EzySimpleZone;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPluginManager;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;

public class EzySimpleZoneTest {

    @Test
    public void propertiesTest() {
        // given
        EzyClient client = mock(EzyClient.class);
        int zoneId = new Random().nextInt();
        String zoneName = "testZoneName";
        EzyZone zone = new EzySimpleZone(client, zoneId, zoneName);

        // when
        int actualZoneId = zone.getId();
        String actualZoneName = zone.getName();
        EzyClient actualClient = zone.getClient();
        EzyAppManager actualAppManager = zone.getAppManager();
        EzyPluginManager actualPluginManager = zone.getPluginManager();
        EzyApp actualApp = zone.getApp();
        EzyPlugin actualPlugin = zone.getPlugin();

        // then
        assert actualZoneId == zoneId;
        assert actualZoneName.equals(zoneName);
        assert actualClient == client;
        assert actualAppManager != null;
        assert actualPluginManager != null;
        assert actualApp == null;
        assert actualPlugin == null;
        assert zone.hashCode() == zoneId;
        System.out.println(zone);
    }
}
