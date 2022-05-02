package com.tvd12.ezyfoxserver.client.testing.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyLoginRequest;
import org.testng.annotations.Test;

public class EzyLoginRequestTest {

    @Test
    public void serialize() {
        // given
        String zoneName = "testZoneName";
        String username = "testUsername";
        String password = "testPassword";
        EzyData data = EzyEntityArrays.newArray("test");
        EzyLoginRequest request = new EzyLoginRequest(
            zoneName,
            username,
            password,
            data
        );

        // when
        EzyData actual = request.serialize();

        // then
        EzyData expected = EzyEntityArrays.newArray(
            zoneName,
            username,
            password,
            data
        );
        assert actual.equals(expected);
        assert request.getCommand() == EzyCommand.LOGIN;
    }

    @Test
    public void serializeWithNullData() {
        // given
        String zoneName = "testZoneName";
        String username = "testUsername";
        String password = "testPassword";
        EzyLoginRequest request = new EzyLoginRequest(
            zoneName,
            username,
            password
        );

        // when
        EzyData actual = request.serialize();

        // then
        EzyData expected = EzyEntityArrays.newArray(
            zoneName,
            username,
            password,
            null
        );
        assert actual.equals(expected);
        assert request.getCommand() == EzyCommand.LOGIN;
    }
}
