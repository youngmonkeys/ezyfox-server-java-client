package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzyConnectionType;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzyConnectionTypeTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(EzyConnectionType.SOCKET.getId(), 1);
        Asserts.assertEquals(EzyConnectionType.SOCKET.getName(), "SOCKET");
    }
}
