package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.util.EzyReleasable;

public interface EzyPackage extends EzyReleasable {

    EzyArray getData();
    
    EzyConstant getTransportType();
    
}
