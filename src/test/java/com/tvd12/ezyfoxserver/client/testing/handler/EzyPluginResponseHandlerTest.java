package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginResponseHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EzyPluginResponseHandlerTest {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void handlePluginNotNullDataHandlerNotNull() {
        // given
        int pluginId = new Random().nextInt();
        String cmd = "testCommand";
        EzyData responseData = EzyEntityArrays.newArray("test");
        EzyArray commandData = EzyEntityArrays.newArray(
            cmd,
            responseData
        );
        EzyArray data = EzyEntityArrays.newArray(
            pluginId,
            commandData
        );
        EzyPlugin plugin = mock(EzyPlugin.class);
        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.getPluginById(pluginId)).thenReturn(plugin);
        EzyPluginDataHandler dataHandler = mock(EzyPluginDataHandler.class);
        when(plugin.getDataHandler(cmd)).thenReturn(dataHandler);
        EzyPluginResponseHandler sut = new EzyPluginResponseHandler();
        sut.setClient(client);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).getPluginById(pluginId);
        verify(plugin, times(1)).getDataHandler(cmd);
        verify(dataHandler, times(1)).handle(plugin, responseData);
    }

    @Test
    public void handlePluginNotNullDataHandlerNull() {
        // given
        int pluginId = new Random().nextInt();
        String cmd = "testCommand";
        EzyData responseData = EzyEntityArrays.newArray("test");
        EzyArray commandData = EzyEntityArrays.newArray(
            cmd,
            responseData
        );
        EzyArray data = EzyEntityArrays.newArray(
            pluginId,
            commandData
        );
        EzyPlugin plugin = mock(EzyPlugin.class);
        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.getPluginById(pluginId)).thenReturn(plugin);
        when(plugin.getDataHandler(cmd)).thenReturn(null);
        EzyPluginResponseHandler sut = new EzyPluginResponseHandler();
        sut.setClient(client);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).getPluginById(pluginId);
        verify(plugin, times(1)).getDataHandler(cmd);
    }

    @Test
    public void handlePluginNull() {
        // given
        int pluginId = new Random().nextInt();
        String cmd = "testCommand";
        EzyData responseData = EzyEntityArrays.newArray("test");
        EzyArray commandData = EzyEntityArrays.newArray(
            cmd,
            responseData
        );
        EzyArray data = EzyEntityArrays.newArray(
            pluginId,
            commandData
        );
        EzyClientForTest client = mock(EzyClientForTest.class);
        when(client.getPluginById(pluginId)).thenReturn(null);
        EzyPluginResponseHandler sut = new EzyPluginResponseHandler();
        sut.setClient(client);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).getPluginById(pluginId);
    }
}
