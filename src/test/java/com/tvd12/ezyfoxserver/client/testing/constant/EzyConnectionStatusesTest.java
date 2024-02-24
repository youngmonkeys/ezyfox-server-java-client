package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatuses;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzyConnectionStatusesTest {

    @Test
    public void isClientConnectableTest() {
        // given
        // when
        // then
        Asserts.assertTrue(
            EzyConnectionStatuses.isClientConnectable(
                EzyConnectionStatus.NULL
            )
        );
        Asserts.assertTrue(
            EzyConnectionStatuses.isClientConnectable(
                EzyConnectionStatus.DISCONNECTED
            )
        );
        Asserts.assertTrue(
            EzyConnectionStatuses.isClientConnectable(
                EzyConnectionStatus.FAILURE
            )
        );
        Asserts.assertFalse(
            EzyConnectionStatuses.isClientReconnectable(
                EzyConnectionStatus.CONNECTED
            )
        );
    }

    @Test
    public void isClientReconnectableTest() {
        // given
        // when
        // then
        Asserts.assertTrue(
            EzyConnectionStatuses.isClientReconnectable(
                EzyConnectionStatus.DISCONNECTED
            )
        );
        Asserts.assertTrue(
            EzyConnectionStatuses.isClientReconnectable(
                EzyConnectionStatus.FAILURE
            )
        );
        Asserts.assertFalse(
            EzyConnectionStatuses.isClientReconnectable(
                EzyConnectionStatus.CONNECTED
            )
        );
    }
}
