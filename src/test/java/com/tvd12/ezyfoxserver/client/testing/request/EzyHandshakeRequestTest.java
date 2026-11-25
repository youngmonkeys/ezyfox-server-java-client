package com.tvd12.ezyfoxserver.client.testing.request;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyHandshakeRequest;
import org.testng.annotations.Test;

public class EzyHandshakeRequestTest {

    @Test
    public void serializeTest() {
        // given
        String clientId = "testClientId";
        byte[] clientKey = new byte[0];
        String clientType = "testClientType";
        String clientVersion = "testClientVersion";
        boolean enableEncryption = true;
        String token = "testToken";
        EzyHandshakeRequest request = new EzyHandshakeRequest(
            clientId,
            clientKey,
            clientType,
            clientVersion,
            enableEncryption,
            token
        );

        // when
        EzyData actual = request.serialize();

        // then
        EzyData expected = EzyEntityArrays.newArray(
            clientId,
            clientKey,
            clientType,
            clientVersion,
            enableEncryption,
            token,
            null,
            false
        );
        assert actual.equals(expected);
        assert request.getCommand() == EzyCommand.HANDSHAKE;
    }

    @Test
    public void serializeWithCompressionTest() {
        // given
        EzyHandshakeRequest request = new EzyHandshakeRequest(
            "testClientId",
            new byte[0],
            "testClientType",
            "testClientVersion",
            false,
            "testToken",
            true
        );

        // when
        EzyArray actual = (EzyArray) request.serialize();

        // then
        assert actual.size() == 8;
        assert actual.get(6) == null;
        assert actual.get(7, boolean.class);
    }
}
