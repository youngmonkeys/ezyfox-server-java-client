package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;
import com.tvd12.ezyfoxserver.client.socket.EzySimplePackage;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

public class EzySimplePackageTest {

    @Test
    public void releaseTest() {
        // given
        EzySimplePackage instance = new EzySimplePackage(
            EzyEntityFactory.newArray(),
            RandomUtil.randomBoolean(),
            RandomUtil.randomShortByteArray(),
            EzyTransportType.TCP
        );

        // when
        instance.release();

        // then
        Asserts.assertNull(instance.getData());
        Asserts.assertNull(instance.getEncryptionKey());
        Asserts.assertNull(instance.getTransportType());
    }
}
