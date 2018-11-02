package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyResettable;

public interface EzySocketEventHandler extends EzyDestroyable, EzyResettable {

    void handleEvent();
    
}
