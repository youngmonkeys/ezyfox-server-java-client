package com.tvd12.ezyfoxserver.client.testing.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyPluginInfoRequest;
import org.testng.annotations.Test;

public class EzyPluginInfoRequestTest {

    @Test
    public void serialize() {
        // given
        String pluginName = "testPluginName";
        EzyPluginInfoRequest request = new EzyPluginInfoRequest(
            pluginName
        );

        // when
        EzyData actual = request.serialize();

        // then
        EzyData expected = EzyEntityArrays.newArray(pluginName);
        assert actual.equals(expected);
        assert request.getCommand() == EzyCommand.PLUGIN_INFO;
    }
}
