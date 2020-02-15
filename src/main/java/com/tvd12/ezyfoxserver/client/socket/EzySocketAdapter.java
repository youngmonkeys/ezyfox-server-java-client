package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.util.EzyLoggable;

public abstract class EzySocketAdapter extends EzyLoggable {
    protected volatile boolean active;
    protected volatile boolean stopped;
    protected final Object adapterLock;

    public EzySocketAdapter() {
        this.active = false;
        this.stopped = false;
        this.adapterLock = new Object();
    }

    public void start() {
        synchronized (adapterLock) {
            if (active)
                return;
            active = true;
            stopped = false;
            Thread newThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    loop();
                }
            });
            newThread.setName(getThreadName());
            newThread.start();
        }
    }

    protected abstract String getThreadName();

    protected void loop() {
        update();
        setStopped(true);
    }

    protected abstract void update();

    public void stop() {
        synchronized (adapterLock) {
        	clear();
            active = false;
        }
    }
    
    protected void clear() {
    }

    protected void setActive(boolean active)
    {
        synchronized (adapterLock) {
            this.active = active;
        }
    }

    protected void setStopped(boolean stopped)
    {
        synchronized (adapterLock) {
            this.stopped = stopped;
        }
    }

    public boolean isActive() {
        synchronized (adapterLock) {
            return active;
        }
    }

    public boolean isStopped() {
        synchronized (adapterLock) {
            return stopped;
        }
    }
}
