package com.tvd12.ezyfoxserver.client.testing.setup;

import com.tvd12.ezyfoxserver.client.event.EzyEventType;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyEventHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandlers;
import com.tvd12.ezyfoxserver.client.manager.EzyHandlerManager;
import com.tvd12.ezyfoxserver.client.setup.*;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;


public class EzySimpleSetupTest {

    @Test
    public void addDataHandler() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        String cmd = Long.toHexString(new Random().nextLong());
        EzyDataHandler dataHandler = mock(EzyDataHandler.class);

        doNothing().when(handlerManager).addDataHandler(cmd, dataHandler);

        // when
        EzySimpleSetup setup = new EzySimpleSetup(handlerManager);
        setup.addDataHandler(cmd, dataHandler);

        // then
        verify(handlerManager, times(1)).addDataHandler(cmd, dataHandler);
    }

    @Test
    public void addEventHandler() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        EzyEventType eventType = EzyEventType.CONNECTION_SUCCESS;
        EzyEventHandler<?> eventHandler = mock(EzyEventHandler.class);

        doNothing().when(handlerManager).addEventHandler(eventType, eventHandler);

        // when
        EzySimpleSetup setup = new EzySimpleSetup(handlerManager);
        setup.addEventHandler(eventType, eventHandler);

        // then
        verify(handlerManager, times(1)).addEventHandler(eventType, eventHandler);
    }

    @Test
    public void setupApp() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        String appName = Long.toHexString(new Random().nextLong());
        EzyAppDataHandlers appDataHandlers = new EzyAppDataHandlers();

        when(handlerManager.getAppDataHandlers(appName)).thenReturn(appDataHandlers);

        // when
        EzySimpleSetup setup = new EzySimpleSetup(handlerManager);
        EzyAppSetup actual = setup.setupApp(appName);

        // then
        EzyAppSetup expected = new EzySimpleAppSetup(appDataHandlers, setup);
        assertEquals(actual.getClass(), expected.getClass());

        verify(handlerManager, times(1)).getAppDataHandlers(appName);
    }

    @Test
    public void setupAppAvailable() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        String appName = Long.toHexString(new Random().nextLong());
        EzyAppDataHandlers appDataHandlers = new EzyAppDataHandlers();

        when(handlerManager.getAppDataHandlers(appName)).thenReturn(appDataHandlers);

        // when
        EzySimpleSetup setup = new EzySimpleSetup(handlerManager);
        EzyAppSetup setupApp = setup.setupApp(appName);
        EzyAppSetup actual = setup.setupApp(appName);

        // then
        assert actual == setupApp;

        verify(handlerManager, times(1)).getAppDataHandlers(appName);
    }

    @Test
    public void setupPlugin() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        String pluginName = Long.toHexString(new Random().nextLong());
        EzyPluginDataHandlers pluginDataHandlers = new EzyPluginDataHandlers();

        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(pluginDataHandlers);

        // when
        EzySimpleSetup setup = new EzySimpleSetup(handlerManager);
        EzyPluginSetup actual = setup.setupPlugin(pluginName);

        // then
        EzyPluginSetup expected = new EzySimplePluginSetup(pluginDataHandlers, setup);
        assertEquals(actual.getClass(), expected.getClass());

        verify(handlerManager, times(1)).getPluginDataHandlers(pluginName);
    }

    @Test
    public void setupPluginAvailable() {
        // given
        EzyHandlerManager handlerManager = mock(EzyHandlerManager.class);
        String pluginName = Long.toHexString(new Random().nextLong());
        EzyPluginDataHandlers pluginDataHandlers = new EzyPluginDataHandlers();

        when(handlerManager.getPluginDataHandlers(pluginName)).thenReturn(pluginDataHandlers);

        // when
        EzySimpleSetup setup = new EzySimpleSetup(handlerManager);
        EzyPluginSetup pluginSetup = setup.setupPlugin(pluginName);
        EzyPluginSetup actual = setup.setupPlugin(pluginName);

        // then
        assertEquals(actual, pluginSetup);

        verify(handlerManager, times(1)).getPluginDataHandlers(pluginName);
    }
}
