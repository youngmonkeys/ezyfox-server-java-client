package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.socket.EzySocketDataEncoder;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzySocketDataEncoderTest {

    private static final byte[] PLAIN = new byte[]{1};
    private static final byte[] ENCRYPTED = new byte[]{2};

    private final EzySocketDataEncoder sut = new EzySocketDataEncoder() {
        @Override
        public byte[] encode(Object data) {
            return PLAIN;
        }

        @Override
        public byte[] encode(Object data, byte[] encryptionKey) {
            return ENCRYPTED;
        }
    };

    @Test
    public void encodeWithCompressionThresholdAndEncryptionKey() throws Exception {
        // given
        byte[] encryptionKey = new byte[]{1, 2, 3};

        // when
        byte[] actual = sut.encode("data", encryptionKey, 128);

        // then
        Asserts.assertEquals(actual, ENCRYPTED);
    }

    @Test
    public void encodeWithCompressionThresholdAndNoEncryptionKey() throws Exception {
        // given
        // when
        byte[] actual = sut.encode("data", null, 128);

        // then
        Asserts.assertEquals(actual, PLAIN);
    }
}
