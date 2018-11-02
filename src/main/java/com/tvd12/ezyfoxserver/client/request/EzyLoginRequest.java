package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;

/**
 * Created by tavandung12 on 10/1/18.
 */

public class EzyLoginRequest implements EzyRequest {
	private static final long serialVersionUID = 6819547581926090345L;
	
	private final String zoneName;
    private final String username;
    private final String password;
    private final EzyData data;

    public EzyLoginRequest(String zoneName,
                           String username,
                           String password) {
        this(zoneName, username, password, null);
    }

    public EzyLoginRequest(String zoneName,
                           String username,
                           String password,
                           EzyData data) {
        this.zoneName = zoneName;
        this.username = username;
        this.password = password;
        this.data = data;
    }

    @Override
    public Object getCommand() {
        return EzyCommand.LOGIN;
    }

    @Override
    public EzyData serialize() {
        EzyData answer = EzyEntityFactory.newArrayBuilder()
                .append(zoneName)
                .append(username)
                .append(password)
                .append(data).build();
        return answer;
    }
}
