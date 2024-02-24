package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzyConnectionFailedReason;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzyConnectionFailedReasonTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(EzyConnectionFailedReason.UNKNOWN.getId(), 4);
        Asserts.assertEquals(EzyConnectionFailedReason.UNKNOWN.getName(), "UNKNOWN");
    }
}
