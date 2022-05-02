package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.entity.EzyData;

import java.io.Serializable;

public interface EzyRequest extends Serializable {

    Object getCommand();

    EzyData serialize();
}
