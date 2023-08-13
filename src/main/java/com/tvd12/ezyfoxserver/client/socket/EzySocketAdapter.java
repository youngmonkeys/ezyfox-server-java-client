package com.tvd12.ezyfoxserver.client.socket;

import com.tvd12.ezyfox.util.EzyLoggable;

import java.nio.channels.AsynchronousCloseException;

public abstract class EzySocketAdapter extends EzyLoggable {
    protected final Object adapterLock;
    protected volatile boolean active;
    protected volatile boolean stopped;

    public EzySocketAdapter() {
        this.active = false;
        this.stopped = false;
        this.adapterLock = new Object();
    }

    public void start() {
        synchronized (adapterLock) {
            if (active) {
                return;
            }
            active = true;
            stopped = false;
            Thread newThread = new Thread(this::loop);
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

    public boolean isActive() {
        synchronized (adapterLock) {
            return active;
        }
    }

    protected void setActive(boolean active) {
        synchronized (adapterLock) {
            this.active = active;
        }
    }

    public boolean isStopped() {
        synchronized (adapterLock) {
            return stopped;
        }
    }

    protected void setStopped(boolean stopped) {
        synchronized (adapterLock) {
            this.stopped = stopped;
        }
    }

    protected void handleSocketReaderException(Exception e) {
        if (e instanceof AsynchronousCloseException) {
            logger.debug("Socket closed by another thread", e);
        } else {
            logger.info("I/O error at socket-reader", e);
        }
    }
}
