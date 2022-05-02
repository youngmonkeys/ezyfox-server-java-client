package com.tvd12.ezyfoxserver.client.testing.entity;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.entity.EzySimplePlugin;
import com.tvd12.ezyfoxserver.client.entity.EzyZone;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandlers;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.*;

public class EzySimplePluginTest {

    @Test
    public void sendRequest() {
        // given
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        EzyZone zone = mock(EzyZone.class);

        EzyClient client = mock(EzyClient.class);
        when(zone.getClient()).thenReturn(client);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyPluginDataHandlers dataHandlers = new EzyPluginDataHandlers();
        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(dataHandlers);

        EzyRequest request = mock(EzyRequest.class);
        String cmd = "testCommand";
        when(request.getCommand()).thenReturn(cmd);
        EzyData requestData = EzyEntityArrays.newArray("test");
        when(request.serialize()).thenReturn(requestData);

        EzySimplePlugin plugin = new EzySimplePlugin(zone, pluginId, pluginName);

        // when
        plugin.send(request);

        // then
        verify(handlerManager, times(1)).getPluginDataHandlers(pluginName);

        verify(client, times(1)).getHandlerManager();

        EzyArray commandData = EzyEntityFactory.newArray();
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(pluginId, commandData);
        verify(client, times(1)).send(EzyCommand.PLUGIN_REQUEST, finalRequestData, false);
    }

    @Test
    public void sendEmptyRequestData() {
        // given
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        EzyZone zone = mock(EzyZone.class);

        EzyClient client = mock(EzyClient.class);
        when(zone.getClient()).thenReturn(client);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyPluginDataHandlers dataHandlers = new EzyPluginDataHandlers();
        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(dataHandlers);

        String cmd = "testCommand";

        EzySimplePlugin plugin = new EzySimplePlugin(zone, pluginId, pluginName);

        // when
        plugin.send(cmd);

        // then
        verify(handlerManager, times(1)).getPluginDataHandlers(pluginName);

        verify(client, times(1)).getHandlerManager();

        EzyArray commandData = EzyEntityFactory.newArray();
        EzyData requestData = EzyEntityFactory.EMPTY_OBJECT;
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(pluginId, commandData);
        verify(client, times(1)).send(EzyCommand.PLUGIN_REQUEST, finalRequestData, false);
    }

    @Test
    public void udpSendRequest() {
        // given
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        EzyZone zone = mock(EzyZone.class);

        EzyClient client = mock(EzyClient.class);
        when(zone.getClient()).thenReturn(client);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyPluginDataHandlers dataHandlers = new EzyPluginDataHandlers();
        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(dataHandlers);

        EzyRequest request = mock(EzyRequest.class);
        String cmd = "testCommand";
        when(request.getCommand()).thenReturn(cmd);
        EzyData requestData = EzyEntityArrays.newArray("test");
        when(request.serialize()).thenReturn(requestData);

        EzySimplePlugin plugin = new EzySimplePlugin(zone, pluginId, pluginName);

        // when
        plugin.udpSend(request);

        // then
        verify(handlerManager, times(1)).getPluginDataHandlers(pluginName);

        verify(client, times(1)).getHandlerManager();

        EzyArray commandData = EzyEntityFactory.newArray();
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(pluginId, commandData);
        verify(client, times(1)).udpSend(EzyCommand.PLUGIN_REQUEST, finalRequestData);
    }

    @Test
    public void udpSendEmptyRequestData() {
        // given
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        EzyZone zone = mock(EzyZone.class);

        EzyClient client = mock(EzyClient.class);
        when(zone.getClient()).thenReturn(client);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyPluginDataHandlers dataHandlers = new EzyPluginDataHandlers();
        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(dataHandlers);

        String cmd = "testCommand";

        EzySimplePlugin plugin = new EzySimplePlugin(zone, pluginId, pluginName);

        // when
        plugin.udpSend(cmd);

        // then
        verify(handlerManager, times(1)).getPluginDataHandlers(pluginName);

        verify(client, times(1)).getHandlerManager();

        EzyArray commandData = EzyEntityFactory.newArray();
        EzyData requestData = EzyEntityFactory.EMPTY_OBJECT;
        commandData.add(cmd, requestData);
        EzyArray finalRequestData = EzyEntityFactory.newArray();
        finalRequestData.add(pluginId, commandData);
        verify(client, times(1)).udpSend(EzyCommand.PLUGIN_REQUEST, finalRequestData);
    }

    @Test
    public void propertiesTest() {
        // given
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        EzyZone zone = mock(EzyZone.class);

        EzyClient client = mock(EzyClient.class);
        when(zone.getClient()).thenReturn(client);

        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        when(client.getHandlerManager()).thenReturn(handlerManager);

        EzyPluginDataHandlers dataHandlers = new EzyPluginDataHandlers();
        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(dataHandlers);

        String cmd = "testCommand";
        EzyPluginDataHandler<?> dataHandler = mock(EzyPluginDataHandler.class);
        dataHandlers.addHandler(cmd, dataHandler);

        EzyRequest request = mock(EzyRequest.class);
        when(request.getCommand()).thenReturn(cmd);
        EzyData requestData = EzyEntityArrays.newArray("test");
        when(request.serialize()).thenReturn(requestData);

        EzySimplePlugin plugin = new EzySimplePlugin(zone, pluginId, pluginName);

        // when
        int actualPluginId = plugin.getId();
        String actualPluginName = plugin.getName();
        EzyClient actualClient = plugin.getClient();
        EzyZone actualZone = plugin.getZone();
        EzyPluginDataHandler<?> actualDataHandler = plugin.getDataHandler(cmd);

        // then
        assert actualPluginId == pluginId;
        assert actualPluginName.equals(pluginName);
        assert actualClient == client;
        assert actualZone == zone;
        assert actualDataHandler == dataHandler;
        assert plugin.hashCode() == pluginId;
        assert plugin.equals(new EzySimplePlugin(zone, pluginId, pluginName));
        System.out.println(plugin);
    }
}
