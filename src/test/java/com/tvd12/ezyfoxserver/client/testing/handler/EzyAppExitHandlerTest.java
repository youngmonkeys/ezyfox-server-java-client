package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.entity.EzyApp;
import com.tvd12.ezyfoxserver.client.entity.EzySimpleApp;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.handler.EzyAppExitHandler;
import com.tvd12.ezyfoxserver.client.manager.EzyAppManager;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.*;

public class EzyAppExitHandlerTest {

    @Test
    public void handleWithApp() {
        // given
        String appName = "testAppName";
        int appId = new Random().nextInt();
        int reasonId = new Random().nextInt();
        EzyZone zone = mock(EzyZone.class);

        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.getZone()).thenReturn(zone);

        EzyAppManager appManager = mock(EzyAppManager.class);
        when(zone.getAppManager()).thenReturn(appManager);
        when(zone.getClient()).thenReturn(client);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyArray data = EzyEntityArrays.newArray(
            appId,
            reasonId
        );
        EzyApp app = new EzySimpleApp(zone, appId, appName);
        when(appManager.removeApp(appId)).thenReturn(app);

        EzyAppExitHandler sut = new EzyAppExitHandler();
        sut.setClient(client);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).getZone();
        verify(zone, times(1)).getAppManager();
        verify(appManager, times(1)).removeApp(appId);
    }

    @Test
    public void handleWithNoApp() {
        // given
        int appId = new Random().nextInt();
        int reasonId = new Random().nextInt();
        EzyZone zone = mock(EzyZone.class);

        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.getZone()).thenReturn(zone);

        EzyAppManager appManager = mock(EzyAppManager.class);
        when(zone.getAppManager()).thenReturn(appManager);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyArray data = EzyEntityArrays.newArray(
            appId,
            reasonId
        );
        when(appManager.removeApp(appId)).thenReturn(null);

        EzyAppExitHandler sut = new EzyAppExitHandler();
        sut.setClient(client);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).getZone();
        verify(zone, times(1)).getAppManager();
        verify(appManager, times(1)).removeApp(appId);
    }
}
