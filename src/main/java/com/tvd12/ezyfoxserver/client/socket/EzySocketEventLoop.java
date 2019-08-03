package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.concurrent.EzyThreadList;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyResettable;
import com.tvd12.ezyfox.util.EzyStartable;

public abstract class EzySocketEventLoop
		extends EzyLoggable
		implements EzyStartable, EzyDestroyable, EzyResettable {

	protected volatile boolean active;
	protected EzyThreadList threadPool;
	
	protected abstract String threadName();
	protected abstract int threadPoolSize();
	
	@Override
	public void start() throws Exception {
	    initThreadPool();
		setActive(true);
		startLoopService();
	}
	
	protected void setActive(boolean value) {
		this.active = value;
	}
	
	private void startLoopService() {
		threadPool.execute();
	}
	
	private Runnable newServiceTask() {
		return () -> eventLoop();
	}
	
	protected abstract void eventLoop();
	
	protected void initThreadPool() {
	    Runnable task = newServiceTask();
	    threadPool = new EzyThreadList(threadPoolSize(), task, threadName());
	}

	@Override
	public void reset() {
		active = true;
	}

	@Override
	public void destroy() {
		try {
			destroy0();
		} catch (Exception e) {
			logger.error("destroy socket event loop error", e);
		}
	}
	
	protected void destroy0() throws Exception {
		setActive(false);
		logger.error("{} stopped", getClass().getSimpleName());
	}
	
}
