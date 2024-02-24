package com.tvd12.ezyfoxserver.client.testing.config;

import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.config.EzyPingConfig;
import com.tvd12.ezyfoxserver.client.config.EzySocketClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzySslType;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Random;

public class EzyClientConfigTest {

    @Test
    public void propertiesTest() {
        // given
        EzyClientConfig clientConfig = EzyClientConfig.builder()
            .pingConfigBuilder()
            .done()
            .build();

        // when
        EzyPingConfig actualPingConfig = clientConfig.getPing();

        // then
        assert actualPingConfig != null;
    }

    @Test
    public void test() {
        // given
        String zoneName = "testZone";
        String clientName = "testClient";
        int maxReconnectCount = new Random().nextInt();
        int reconnectPeriod = new Random().nextInt();

        EzyClientConfig config = EzyClientConfig.builder()
            .zoneName(zoneName)
            .clientName(clientName)
            .reconnectConfigBuilder()
            .enable(true)
            .maxReconnectCount(maxReconnectCount)
            .reconnectPeriod(reconnectPeriod)
            .done()
            .build();

        // when
        // then
        assert config.getClientName().equals(clientName);
        assert config.getZoneName().equals(zoneName);
        assert config.getReconnect().isEnable();
        assert config.getReconnect().getMaxReconnectCount() == maxReconnectCount;
        assert config.getReconnect().getReconnectPeriod() == reconnectPeriod;
        assert config.getSslType() == EzySslType.CUSTOMIZATION;
        assert !config.isEnableEncryption();
        assert !config.isEnableCertificationSSL();
    }

    @Test
    public void testClientNameNull() {
        // given
        String zoneName = "testZone";

        EzyClientConfig config = EzyClientConfig.builder()
            .zoneName(zoneName)
            .build();

        // when
        // then
        assert config.getClientName().equals(zoneName);
    }

    @Test
    public void isEnableEncryptionTest() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .enableSSL()
            .sslType(EzySslType.CUSTOMIZATION)
            .build();

        // when
        // then
        Asserts.assertTrue(config.isEnableEncryption());
        Asserts.assertFalse(config.isEnableCertificationSSL());
    }

    @Test
    public void isNotEnableSslTest() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .sslType(EzySslType.CUSTOMIZATION)
            .build();

        // when
        // then
        Asserts.assertFalse(config.isEnableEncryption());
        Asserts.assertFalse(config.isEnableCertificationSSL());
    }

    @Test
    public void isEnableCertificationSSLTest() {
        // given
        EzyClientConfig config = EzyClientConfig.builder()
            .enableSSL()
            .sslType(EzySslType.CERTIFICATION)
            .build();

        // when
        // then
        Asserts.assertFalse(config.isEnableEncryption());
        Asserts.assertTrue(config.isEnableCertificationSSL());
    }

    @Test
    public void defaultFunctionsTest() {
        // given
        EzySocketClientConfig config = new TestClientConfig();

        // when
        // then
        assert !config.isEnableSSL();
        assert config.getSslType() == EzySslType.CUSTOMIZATION;
        assert !config.isEnableEncryption();
        assert !config.isEnableCertificationSSL();
    }


    public static class TestClientConfig implements EzySocketClientConfig {}
}
