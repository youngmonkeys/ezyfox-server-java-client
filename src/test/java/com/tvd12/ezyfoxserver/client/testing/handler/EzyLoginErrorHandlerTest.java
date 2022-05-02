package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.handler.EzyLoginErrorHandler;
import com.tvd12.ezyfoxserver.client.testing.EzyClientForTest;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class EzyLoginErrorHandlerTest {

    @Test
    public void handle() {
        // given
        EzyClientForTest client = mock(EzyClientForTest.class);

        EzyArray data = EzyEntityArrays.newArray();
        EzyLoginErrorHandler sut = new EzyLoginErrorHandler();
        sut.setClient(client);

        // when
        sut.handle(data);

        // then
        verify(client, times(1)).disconnect(EzyDisconnectReason.UNAUTHORIZED.getId());
    }
}
