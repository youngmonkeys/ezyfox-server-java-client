package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzyTransportTypeTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(EzyTransportType.TCP.getId(), 1);
        Asserts.assertEquals(EzyTransportType.TCP.getName(), "TCP");
    }
}
