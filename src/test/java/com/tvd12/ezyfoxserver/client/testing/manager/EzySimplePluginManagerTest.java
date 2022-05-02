package com.tvd12.ezyfoxserver.client.testing.manager;

import com.tvd12.ezyfoxserver.client.entity.EzyPlugin;
import com.tvd12.ezyfoxserver.client.manager.EzySimplePluginManager;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class EzySimplePluginManagerTest {

    @Test
    public void getPlugin() {
        // given
        String zoneName = "testZoneName";
        EzySimplePluginManager sut = new EzySimplePluginManager(zoneName);
        EzyPlugin plugin = mock(EzyPlugin.class);
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        when(plugin.getId()).thenReturn(pluginId);
        when(plugin.getName()).thenReturn(pluginName);
        sut.addPlugin(plugin);

        // when
        EzyPlugin actualPlugin = sut.getPlugin();
        EzyPlugin actualPluginById = sut.getPluginById(pluginId);
        EzyPlugin actualPluginByName = sut.getPluginByName(pluginName);

        // then
        assert actualPlugin == plugin;
        assert actualPluginById == plugin;
        assert actualPluginByName == plugin;
        verify(plugin, times(1)).getId();
        verify(plugin, times(1)).getName();
    }

    @Test
    public void removePlugin() {
        // given
        String zoneName = "testZoneName";
        EzySimplePluginManager sut = new EzySimplePluginManager(zoneName);
        EzyPlugin plugin = mock(EzyPlugin.class);
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        when(plugin.getId()).thenReturn(pluginId);
        when(plugin.getName()).thenReturn(pluginName);
        sut.addPlugin(plugin);

        // when
        EzyPlugin actualPlugin = sut.removePlugin(pluginId);

        // then
        assert actualPlugin == plugin;
        verify(plugin, times(1)).getId();
        verify(plugin, times(2)).getName();
    }

    @Test
    public void removeNotExistedPlugin() {
        // given
        String zoneName = "testZoneName";
        EzySimplePluginManager sut = new EzySimplePluginManager(zoneName);
        EzyPlugin plugin = mock(EzyPlugin.class);
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        when(plugin.getId()).thenReturn(pluginId);
        when(plugin.getName()).thenReturn(pluginName);
        sut.addPlugin(plugin);

        // when
        EzyPlugin actualPlugin = sut.removePlugin(pluginId + 1);

        // then
        assert actualPlugin == null;
        verify(plugin, times(1)).getId();
        verify(plugin, times(1)).getName();
    }

    @Test
    public void getPluginList() {
        // given
        String zoneName = "testZoneName";
        EzySimplePluginManager sut = new EzySimplePluginManager(zoneName);
        EzyPlugin plugin = mock(EzyPlugin.class);
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        when(plugin.getId()).thenReturn(pluginId);
        when(plugin.getName()).thenReturn(pluginName);
        sut.addPlugin(plugin);

        // when
        List<EzyPlugin> actualPlugins = sut.getPluginList();

        // then
        assertEquals(actualPlugins, Collections.singletonList(plugin));
        verify(plugin, times(1)).getId();
        verify(plugin, times(1)).getName();
    }

    @Test
    public void clear() {
        // given
        String zoneName = "testZoneName";
        EzySimplePluginManager sut = new EzySimplePluginManager(zoneName);
        EzyPlugin plugin = mock(EzyPlugin.class);
        int pluginId = new Random().nextInt();
        String pluginName = "testPluginName";
        when(plugin.getId()).thenReturn(pluginId);
        when(plugin.getName()).thenReturn(pluginName);
        sut.addPlugin(plugin);

        // when
        sut.clear();

        // then
        assert sut.getPluginList().isEmpty();
        verify(plugin, times(1)).getId();
        verify(plugin, times(1)).getName();
    }
}
