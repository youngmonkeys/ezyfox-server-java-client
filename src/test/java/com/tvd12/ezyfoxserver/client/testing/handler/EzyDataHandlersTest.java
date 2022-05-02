package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandler;
import com.tvd12.ezyfoxserver.client.handler.EzyDataHandlers;
import com.tvd12.ezyfoxserver.client.socket.EzyPingSchedule;
import com.tvd12.ezyfoxserver.client.testing.EzyDataHandlerForTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyDataHandlersTest {

    @Test
    public void handleWithHandler() {
        // given
        EzyClient client = mock(EzyClient.class);
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyDataHandlers sut = new EzyDataHandlers(client, pingSchedule);

        String cmd = "testCommand";
        EzyDataHandler dataHandler = mock(EzyDataHandlerForTest.class);
        sut.addHandler(cmd, dataHandler);

        // when
        EzyArray data = EzyEntityArrays.newArray();
        sut.handle(cmd, data);

        // then
        verify(dataHandler, times(1)).handle(data);
    }

    @Test
    public void handleWithNoHandler() {
        // given
        EzyClient client = mock(EzyClient.class);
        EzyPingSchedule pingSchedule = mock(EzyPingSchedule.class);
        EzyDataHandlers sut = new EzyDataHandlers(client, pingSchedule);

        String cmd = "testCommand";
        EzyDataHandler dataHandler = mock(EzyDataHandlerForTest.class);

        // when
        EzyArray data = EzyEntityArrays.newArray();
        sut.handle(cmd, data);

        // then
        verify(dataHandler, times(0)).handle(data);
    }
}
