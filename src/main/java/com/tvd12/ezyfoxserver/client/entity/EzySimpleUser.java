package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.util.EzyEquals;

import lombok.Getter;

/**
 * Created by tavandung12 on 10/2/18.
 */

@Getter
public class EzySimpleUser implements EzyUser {

    protected final long id;
    protected final String name;

    public EzySimpleUser(long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
	public boolean equals(Object obj) {
    	return new EzyEquals<EzySimpleUser>()
    			.function(t -> t.id)
    			.isEquals(this, obj);
	}
    
    @Override
	public int hashCode() {
    	return Long.hashCode(id);
	}
    
    @Override
    public String toString() {
    	return new StringBuilder()
    			.append("User(")
    			.append("id: ").append(id).append(", ")
    			.append("name: ").append(name)
    			.append(")")
    			.toString();
    }
}
