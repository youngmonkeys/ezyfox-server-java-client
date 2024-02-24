package com.tvd12.ezyfoxserver.client.testing.ssl;

import com.tvd12.ezyfoxserver.client.ssl.EzySslContextFactory;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import javax.net.ssl.SSLContext;

public class EzySslContextFactoryTest {

    @Test
    public void test() throws Exception {
        // given
        // when
        SSLContext sslContext = EzySslContextFactory
            .getInstance()
            .newSslContext();

        // then
        Asserts.assertNotNull(sslContext);
    }
}
