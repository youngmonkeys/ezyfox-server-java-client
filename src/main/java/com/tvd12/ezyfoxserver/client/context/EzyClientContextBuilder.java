package com.tvd12.ezyfoxserver.client.context;

import com.tvd12.ezyfoxserver.builder.EzyBuilder;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.serialize.EzyRequestSerializer;
import com.tvd12.ezyfoxserver.client.serialize.impl.EzyRequestSerializerImpl;
import com.tvd12.ezyfoxserver.concurrent.EzyExecutors;

public class EzyClientContextBuilder implements EzyBuilder<EzyClientContext> {

	protected EzyClient client;
	
	public static EzyClientContextBuilder clientContextBuilder() {
		return new EzyClientContextBuilder();
	}
	
	public EzyClientContextBuilder client(EzyClient client) {
		this.client = client;
		return this;
	}
	
	@Override
	public EzyClientContext build() {
		EzySimpleClientContext context = newProduct();
		context.setClient(client);
		context.setProperty(EzyRequestSerializer.class,	newRequestSerializer());
		context.setWorkerExecutor(EzyExecutors.newFixedThreadPool(client.getWorkerPoolSize(), "client-worker"));
		return context;
	}
	
	protected EzySimpleClientContext newProduct() {
		return new EzySimpleClientContext();
	}
	
	 protected EzyRequestSerializer newRequestSerializer() {
		 return EzyRequestSerializerImpl.builder().build();
	 }
	
}
