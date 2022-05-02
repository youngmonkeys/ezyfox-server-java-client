package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.util.EzyEquals;
import lombok.Getter;

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
        return "User(" +
            "id: " + id + ", " +
            "name: " + name +
            ")";
    }
}
