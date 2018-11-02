package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyResettable;
import com.tvd12.ezyfox.util.EzyStartable;

public abstract class EzySocketEventLoopHandler
		extends EzyLoggable
		implements EzyThreadPoolSizeAware, EzyStartable, EzyDestroyable, EzyResettable {

    protected int threadPoolSize = 1;
    protected EzySocketEventLoop eventLoop;
    
	@Override
	public void start() throws Exception {
	    this.eventLoop = newEventLoop0();
	    this.eventLoop.start();
	}
	
	private EzySimpleSocketEventLoop newEventLoop0() {
	    EzySimpleSocketEventLoop eventLoop = newEventLoop();
	    eventLoop.setThreadName(getThreadName());
	    eventLoop.setThreadPoolSize(threadPoolSize);
	    return eventLoop;
	}
	
	protected abstract String getThreadName();
	
	protected abstract EzySimpleSocketEventLoop newEventLoop();

	@Override
	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	@Override
	public void reset() {
		eventLoop.reset();
	}

	@Override
	public void destroy() {
		if(eventLoop != null) {
			try {
				eventLoop.destroy0();
			} catch (Exception e) {
				getLogger().error("destroy " + getClass().getSimpleName() + " error", e);
			}
		}
	}
	
}
