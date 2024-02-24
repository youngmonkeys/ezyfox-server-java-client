package com.tvd12.ezyfoxserver.client.testing.constant;

import com.tvd12.ezyfoxserver.client.constant.EzySocketStatus;
import com.tvd12.ezyfoxserver.client.constant.EzySocketStatuses;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzySocketStatusesTest {

    @Test
    public void isSocketConnectableTest() {
        Asserts.assertTrue(
            EzySocketStatuses.isSocketConnectable(
                EzySocketStatus.NOT_CONNECT
            )
        );
        Asserts.assertTrue(
            EzySocketStatuses.isSocketConnectable(
                EzySocketStatus.DISCONNECTED
            )
        );
        Asserts.assertTrue(
            EzySocketStatuses.isSocketConnectable(
                EzySocketStatus.CONNECT_FAILED
            )
        );
        Asserts.assertFalse(
            EzySocketStatuses.isSocketConnectable(
                EzySocketStatus.CONNECTED
            )
        );
    }

    @Test
    public void isSocketDisconnectableTest() {
        Asserts.assertTrue(
            EzySocketStatuses.isSocketDisconnectable(
                EzySocketStatus.CONNECTED
            )
        );
        Asserts.assertTrue(
            EzySocketStatuses.isSocketDisconnectable(
                EzySocketStatus.DISCONNECTING
            )
        );
        Asserts.assertFalse(
            EzySocketStatuses.isSocketDisconnectable(
                EzySocketStatus.DISCONNECTED
            )
        );
    }
}
