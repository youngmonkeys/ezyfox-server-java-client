package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import org.testng.annotations.Test;

public class EzyDisconnectReasonTest {

    @Test
    public void getName() {
        // given
        EzyDisconnectReason sut = EzyDisconnectReason.IDLE;

        // when
        // then
        assert sut.getName().equals(sut.toString());
    }

    @Test
    public void valueOf() {
        // given
        // when
        // then
        EzyDisconnectReason sut =
            EzyDisconnectReason.valueOf(EzyDisconnectReason.CLOSE.getId());
        assert sut != null;
    }

    @Test
    public void valueOfReturnNull() {
        // given
        // when
        // then
        EzyDisconnectReason sut =
            EzyDisconnectReason.valueOf(-1000);
        assert sut == null;
    }
}
