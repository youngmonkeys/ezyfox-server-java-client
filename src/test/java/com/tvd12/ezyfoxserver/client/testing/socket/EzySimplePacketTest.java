package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;
import com.tvd12.ezyfoxserver.client.socket.EzySimplePacket;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzySimplePacketTest {

    @Test
    public void bytesDataTest() {
        // given
        byte[] data = new byte[]{1, 2, 3};
        EzySimplePacket sut = new EzySimplePacket();

        // when
        sut.setData(data);

        // then
        Asserts.assertEquals(sut.getData(), data);
        Asserts.assertEquals(sut.getSize(), 3);
        Asserts.assertFalse(sut.isFragmented());
        Asserts.assertFalse(sut.isReleased());
        Asserts.assertEquals(sut.getTransportType(), EzyTransportType.TCP);
    }

    @Test
    public void stringDataTest() {
        // given
        EzySimplePacket sut = new EzySimplePacket();

        // when
        sut.setData("hello");

        // then
        Asserts.assertEquals(sut.getSize(), 5);
    }

    @Test
    public void setFragmentTest() {
        // given
        byte[] fragment = new byte[]{1, 2};
        EzySimplePacket sut = new EzySimplePacket();
        sut.setData(new byte[]{0, 1, 2});

        // when
        sut.setFragment(fragment);

        // then
        Asserts.assertTrue(sut.isFragmented());
        Asserts.assertEquals(sut.getData(), fragment);
        Asserts.assertEquals(sut.getSize(), 2);
    }

    @Test
    public void releaseTest() {
        // given
        EzySimplePacket sut = new EzySimplePacket();
        sut.setData(new byte[]{1});

        // when
        sut.release();

        // then
        Asserts.assertTrue(sut.isReleased());
        Asserts.assertNull(sut.getData());
    }

    @Test
    public void transportTypeAndToStringTest() {
        // given
        EzySimplePacket sut = new EzySimplePacket();
        sut.setData("hello");

        // when
        sut.setTransportType(EzyTransportType.UDP);

        // then
        Asserts.assertEquals(sut.getTransportType(), EzyTransportType.UDP);
        Asserts.assertEquals(
            sut.toString(),
            "(transportType: " + EzyTransportType.UDP + ", data: hello)"
        );
    }
}
