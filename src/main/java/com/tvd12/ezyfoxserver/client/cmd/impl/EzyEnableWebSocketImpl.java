package com.tvd12.ezyfoxserver.client.cmd.impl;

import java.util.concurrent.ExecutorService;

import com.tvd12.ezyfox.codec.EzyCodecCreator;
import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.reflect.EzyClasses;
import com.tvd12.ezyfoxserver.client.EzyClient;
import com.tvd12.ezyfoxserver.client.EzyClientBootstrap;
import com.tvd12.ezyfoxserver.client.EzyClientWebSocketBootstrap;
import com.tvd12.ezyfoxserver.client.cmd.EzyEnableWebSocket;
import com.tvd12.ezyfoxserver.client.context.EzyClientContext;
import com.tvd12.ezyfoxserver.client.setting.EzyWebSocketSetting;
import com.tvd12.ezyfoxserver.client.setting.EzyWebSocketSettingBuilder;
import com.tvd12.ezyfoxserver.command.impl.EzyAbstractCommand;

public class EzyEnableWebSocketImpl 
		extends EzyAbstractCommand 
		implements EzyEnableWebSocket {

	protected boolean fromMainThread;
	protected EzyWebSocketSetting socketSetting;
	
	protected final EzyClient client;
	protected final EzyClientContext context;
	
	public EzyEnableWebSocketImpl(EzyClientContext context) {
		this.context = context;
		this.client = context.getClient();
	}
	
	@Override
	public EzyEnableWebSocket fromMainThread(boolean fromMainThread) {
		this.fromMainThread = fromMainThread;
		return this;
	}
	
	@Override
	public EzyEnableWebSocket socketSetting(EzyWebSocketSetting socketSetting) {
		this.socketSetting = socketSetting;
		return this;
	}
	
	@Override
	public EzyEnableWebSocket socketSetting(EzyWebSocketSettingBuilder socketSettingBuilder) {
		return socketSetting(socketSettingBuilder.build());
	}
	
	@Override
	public void execute() throws Exception {
		EzyCodecCreator codecCreator = newCodecCreator();
		ExecutorService startExecutorService = newStartExecutorService();
		EzyClientBootstrap boostrap = EzyClientWebSocketBootstrap.builder()
			.clientContext(context)
			.codecCreator(codecCreator)
			.uri(socketSetting.getUri())
			.startExecutorService(startExecutorService)
			.build();
		boostrap.start();
		context.setProperty(EzyClientBootstrap.class, boostrap);
	}
	
	private EzyCodecCreator newCodecCreator() {
		return EzyClasses.newInstance(socketSetting.getCodecCreator());
	}
	
	private ExecutorService newStartExecutorService() {
		if(fromMainThread) return null;
		ExecutorService answer = EzyExecutors.newSingleThreadExecutor("client-starter");
    		Runtime.getRuntime().addShutdownHook(new Thread(() -> answer.shutdown()));
    		return answer;
	}
	
}
