package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzySocketStatusTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(EzySocketStatus.CONNECTED.getId(), 3);
        Asserts.assertEquals(EzySocketStatus.CONNECTED.getName(), "CONNECTED");
    }
}
