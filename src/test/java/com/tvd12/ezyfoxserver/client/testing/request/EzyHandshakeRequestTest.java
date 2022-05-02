package com.tvd12.ezyfoxserver.client.testing.request;

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
            token
        );
        assert actual.equals(expected);
        assert request.getCommand() == EzyCommand.HANDSHAKE;
    }
}
