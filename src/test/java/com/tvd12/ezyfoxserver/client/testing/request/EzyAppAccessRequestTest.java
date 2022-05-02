package com.tvd12.ezyfoxserver.client.testing.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyAppAccessRequest;
import org.testng.annotations.Test;

public class EzyAppAccessRequestTest {

    @Test
    public void serialize() {
        // given
        String appName = "testAppName";
        EzyData data = EzyEntityArrays.newArray("test");
        EzyAppAccessRequest request = new EzyAppAccessRequest(
            appName,
            data
        );

        // when
        EzyData actual = request.serialize();

        // then
        EzyData expected = EzyEntityArrays.newArray(appName, data);
        assert actual.equals(expected);
        assert request.getCommand() == EzyCommand.APP_ACCESS;
    }

    @Test
    public void serializeWithNullData() {
        // given
        String appName = "testAppName";
        EzyAppAccessRequest request = new EzyAppAccessRequest(
            appName
        );

        // when
        EzyData actual = request.serialize();

        // then
        EzyData expected = EzyEntityArrays.newArray(appName, null);
        assert actual.equals(expected);
        assert request.getCommand() == EzyCommand.APP_ACCESS;
    }
}
