package com.tvd12.ezyfoxserver.client.testing.socket;

import com.tvd12.ezyfox.codec.EzyByteToObjectDecoder;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.codec.EzyMessageReaders;
import com.tvd12.ezyfox.codec.MsgPackCodecCreator;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyTransportType;
import com.tvd12.ezyfoxserver.client.socket.EzyBlockingPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzyPacketQueue;
import com.tvd12.ezyfoxserver.client.socket.EzySimplePackage;
import com.tvd12.ezyfoxserver.client.socket.EzySimpleSocketDataEncoder;
import com.tvd12.ezyfoxserver.client.socket.EzySocketResponseApi;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class EzySocketResponseApiCompressionTest {

    @Test
    public void compressTest() throws Exception {
        // given
        EzyPacketQueue packetQueue = new EzyBlockingPacketQueue();
        MsgPackCodecCreator codecCreator = new MsgPackCodecCreator(false);
        EzySocketResponseApi sut = new EzySocketResponseApi(
            new EzySimpleSocketDataEncoder(codecCreator.newEncoder()),
            packetQueue,
            512
        );
        sut.setCompressionEnable(true);
        EzyArray data = newLongData();

        // when
        sut.response(new EzySimplePackage(data, false, null, EzyTransportType.TCP));

        // then
        EzyMessage message = EzyMessageReaders.bytesToMessage(
            (byte[]) packetQueue.take().getData()
        );
        EzyByteToObjectDecoder decoder = codecCreator.newDecoder(Integer.MAX_VALUE);
        Asserts.assertTrue(message.getHeader().isCompressed());
        EzyArray decoded = (EzyArray) decoder.decode(message, null);
        Asserts.assertEquals(decoded.get(1, String.class), data.get(1, String.class));
    }

    @Test
    public void compressAndEncryptTest() throws Exception {
        // given
        EzyPacketQueue packetQueue = new EzyBlockingPacketQueue();
        MsgPackCodecCreator codecCreator = new MsgPackCodecCreator(true);
        EzySocketResponseApi sut = new EzySocketResponseApi(
            new EzySimpleSocketDataEncoder(codecCreator.newEncoder()),
            packetQueue,
            512
        );
        sut.setCompressionEnable(true);
        EzyArray data = newLongData();
        byte[] key = EzyAesCrypt.randomKey();

        // when
        sut.response(new EzySimplePackage(data, true, key, EzyTransportType.TCP));

        // then
        EzyMessage message = EzyMessageReaders.bytesToMessage(
            (byte[]) packetQueue.take().getData()
        );
        EzyByteToObjectDecoder decoder = codecCreator.newDecoder(Integer.MAX_VALUE);
        Asserts.assertTrue(message.getHeader().isCompressed());
        Asserts.assertTrue(message.getHeader().isEncrypted());
        EzyArray decoded = (EzyArray) decoder.decode(message, key);
        Asserts.assertEquals(decoded.get(1, String.class), data.get(1, String.class));
    }

    @Test
    public void notCompressWhenDisabledOrSmallTest() throws Exception {
        // given
        EzyPacketQueue packetQueue = new EzyBlockingPacketQueue();
        MsgPackCodecCreator codecCreator = new MsgPackCodecCreator(false);
        EzySocketResponseApi sut = new EzySocketResponseApi(
            new EzySimpleSocketDataEncoder(codecCreator.newEncoder()),
            packetQueue,
            512
        );

        // when
        sut.response(new EzySimplePackage(newLongData(), false, null, EzyTransportType.TCP));
        sut.setCompressionEnable(true);
        sut.response(
            new EzySimplePackage(
                EzyEntityArrays.newArray("hello"),
                false,
                null,
                EzyTransportType.TCP
            )
        );

        // then
        EzyMessage disabledMessage = EzyMessageReaders.bytesToMessage(
            (byte[]) packetQueue.take().getData()
        );
        EzyMessage smallMessage = EzyMessageReaders.bytesToMessage(
            (byte[]) packetQueue.take().getData()
        );
        Asserts.assertFalse(disabledMessage.getHeader().isCompressed());
        Asserts.assertFalse(smallMessage.getHeader().isCompressed());
    }

    private static EzyArray newLongData() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 200; ++i) {
            builder.append("hello world ");
        }
        return EzyEntityArrays.newArray(1, builder.toString());
    }
}
