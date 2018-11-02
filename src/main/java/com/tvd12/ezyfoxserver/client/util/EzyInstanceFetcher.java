package com.tvd12.ezyfoxserver.client.util;

public interface EzyInstanceFetcher {
	
	<T> T get(Class<T> clazz);
	
}
