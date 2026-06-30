package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;
import com.tvd12.ezyfoxserver.client.entity.EzySimplePlugin;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginInfoHandler;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.manager.EzyPluginManager;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EzyPluginInfoHandlerTest {

    @Test
    public void handle() {
        // given
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        EzyZone zone = mock(EzyZone.class);

        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.getZone()).thenReturn(zone);

        EzyPluginManager pluginManager = mock(EzyPluginManager.class);
        when(zone.getPluginManager()).thenReturn(pluginManager);
        when(zone.getClient()).thenReturn(client);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyPluginDataHandlers pluginDataHandlers = new EzyPluginDataHandlers();
        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(pluginDataHandlers);

        EzyArray data = EzyEntityArrays.newArray(
            pluginId,
            pluginName
        );
        EzyPluginInfoHandler sut = new EzyPluginInfoHandler();
        sut.setClient(client);

        // when
        sut.handle(data);

        // then
        EzyPlugin plugin = new EzySimplePlugin(zone, pluginId, pluginName);

        verify(client, times(1)).getZone();
        verify(zone, times(1)).getPluginManager();
        verify(pluginManager, times(1)).addPlugin(plugin);
    }
}
