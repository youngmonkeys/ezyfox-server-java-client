package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.util.EzyDestroyable;

/**
 * Created by tavandung12 on 10/2/18.
 */

public interface EzyUser extends EzyDestroyable {

    long getId();

    String getName();

}
