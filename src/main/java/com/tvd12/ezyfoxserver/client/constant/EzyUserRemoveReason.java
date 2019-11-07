package com.tvd12.ezyfoxserver.client.constant;

import java.util.Map;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.util.EzyEnums;

import lombok.Getter;

@Getter
public enum EzyUserRemoveReason implements EzyConstant {

    EXIT_APP(300);
    
    private final int id;

    private static final Map<Integer, EzyUserRemoveReason> MAP = EzyEnums.enumMapInt(EzyUserRemoveReason.class);
    
    private EzyUserRemoveReason(int id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return toString();
    }
    
    public static EzyUserRemoveReason valueOf(int id) {
    	return MAP.get(id);
    }
    
}
