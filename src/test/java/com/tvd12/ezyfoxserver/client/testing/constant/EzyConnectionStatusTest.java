package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzyConnectionStatusTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(EzyConnectionStatus.CONNECTED.getId(), 2);
        Asserts.assertEquals(EzyConnectionStatus.CONNECTED.getName(), "CONNECTED");
    }
}
