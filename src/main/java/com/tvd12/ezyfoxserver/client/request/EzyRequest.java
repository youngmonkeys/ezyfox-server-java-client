package com.tvd12.ezyfoxserver.client.request;

import java.io.Serializable;

import com.tvd12.ezyfox.entity.EzyData;

/**
 * Created by tavandung12 on 10/1/18.
 */

public interface EzyRequest extends Serializable {

    Object getCommand();

    EzyData serialize();

}
