package com.tvd12.ezyfoxserver.client.testing.handler;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.handler.EzyPongHandler;
import org.testng.annotations.Test;

public class EzyPongHandlerTest {

    @Test
    public void handle() {
        // given
        EzyArray data = EzyEntityArrays.newArray();
        EzyPongHandler sut = new EzyPongHandler();

        // when
        // then
        sut.handle(data);
    }
}
