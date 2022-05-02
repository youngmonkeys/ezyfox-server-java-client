package com.tvd12.ezyfoxserver.client.testing.setup;

import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyPluginDataHandlers;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;
import com.tvd12.ezyfoxserver.client.setup.EzySimplePluginSetup;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;

public class EzySimplePluginSetupTest {

    @Test
    public void addDataHandler() {
        // given
        EzyPluginDataHandlers pluginDataHandlers = new EzyPluginDataHandlers();
        EzySetup setup = mock(EzySetup.class);
        String cmd = Long.toHexString(new Random().nextLong());
        EzySimplePluginSetup pluginSetup = new EzySimplePluginSetup(pluginDataHandlers, setup);

        // when
        EzyPluginDataHandler<?> dataHandler = mock(EzyPluginDataHandler.class);
        pluginSetup.addDataHandler(cmd, dataHandler);

        // then
        assert pluginDataHandlers.getHandler(cmd) == dataHandler;
    }

    @Test
    public void done() {
        // given
        EzyPluginDataHandlers pluginDataHandlers = new EzyPluginDataHandlers();
        EzySetup setup = mock(EzySetup.class);
        EzySimplePluginSetup pluginSetup = new EzySimplePluginSetup(pluginDataHandlers, setup);

        // when
        EzySetup actual = pluginSetup.done();

        // then
        assert actual == setup;
    }
}
