package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyReleasable;
import com.tvd12.ezyfoxserver.client.constant.EzyConstant;

public interface EzyPackage extends EzyReleasable {

    EzyArray getData();
    
    EzyConstant getTransportType();
    
}
