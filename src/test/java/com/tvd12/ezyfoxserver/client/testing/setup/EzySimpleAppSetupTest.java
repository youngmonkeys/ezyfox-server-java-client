package com.tvd12.ezyfoxserver.client.testing.setup;

import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyAppDataHandlers;
import com.tvd12.ezyfoxserver.client.setup.EzySetup;
import com.tvd12.ezyfoxserver.client.setup.EzySimpleAppSetup;
import org.testng.annotations.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;

public class EzySimpleAppSetupTest {

    @Test
    public void addDataHandler() {
        // given
        EzyAppDataHandlers pluginDataHandlers = new EzyAppDataHandlers();
        EzySetup setup = mock(EzySetup.class);
        String cmd = Long.toHexString(new Random().nextLong());
        EzySimpleAppSetup pluginSetup = new EzySimpleAppSetup(pluginDataHandlers, setup);

        // when
        EzyAppDataHandler<?> dataHandler = mock(EzyAppDataHandler.class);
        pluginSetup.addDataHandler(cmd, dataHandler);

        // then
        assert pluginDataHandlers.getHandler(cmd) == dataHandler;
    }

    @Test
    public void done() {
        // given
        EzyAppDataHandlers pluginDataHandlers = new EzyAppDataHandlers();
        EzySetup setup = mock(EzySetup.class);
        EzySimpleAppSetup pluginSetup = new EzySimpleAppSetup(pluginDataHandlers, setup);

        // when
        EzySetup actual = pluginSetup.done();

        // then
        assert actual == setup;
    }
}
