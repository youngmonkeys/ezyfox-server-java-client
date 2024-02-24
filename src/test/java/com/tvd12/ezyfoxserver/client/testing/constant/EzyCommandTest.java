package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzyCommandTest {

    @Test
    public void test() {
        // given
        // when
        // then
        Asserts.assertEquals(EzyCommand.ERROR.getPriority(), 10);
        Asserts.assertEquals(EzyCommand.ERROR.getName(), "ERROR");
        Asserts.assertTrue(EzyCommand.LOGIN.isSystemCommand());
        Asserts.assertFalse(EzyCommand.ERROR.isSystemCommand());
        Asserts.assertEquals(
            EzyCommand.ERROR.compareByPriority(EzyCommand.HANDSHAKE),
            10
        );
    }
}
