package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReason;
import com.tvd12.ezyfoxserver.client.constant.EzyDisconnectReasons;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzyDisconnectReasonsTest {

    @Test
    public void getDisconnectReasonNameTest() {
        // given
        // when
        String actual = EzyDisconnectReasons.getDisconnectReasonName(
            EzyDisconnectReason.MAX_REQUEST_PER_SECOND.getId()
        );

        // then
        Asserts.assertEquals(
            actual,
            "MAX_REQUEST_PER_SECOND"
        );
    }

    @Test
    public void getDisconnectReasonNameIntTest() {
        // given
        // when
        String actual = EzyDisconnectReasons.getDisconnectReasonName(
            -1000
        );

        // then
        Asserts.assertEquals(
            actual,
            "-1000"
        );
    }
}
