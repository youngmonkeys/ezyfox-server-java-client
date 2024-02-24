package com.tvd12.ezyfoxserver.client.socket;

import java.util.LinkedList;
import java.util.Queue;

public class EzyBlockingPacketQueue implements EzyPacketQueue {

    protected final int capacity;
    protected final Queue<EzyPacket> queue;
    protected volatile boolean empty = true;
    protected volatile boolean processing = false;

    public EzyBlockingPacketQueue() {
        this(10000);
    }

    public EzyBlockingPacketQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    @Override
    public int size() {
        synchronized (this) {
            return queue.size();
        }
    }

    @Override
    public void clear() {
        synchronized (this) {
            queue.clear();
            empty = true;
            processing = false;
        }
    }

    @Override
    public EzyPacket take() {
        synchronized (this) {
            EzyPacket packet = queue.poll();
            processing = false;
            empty = queue.isEmpty();
            notifyAll();
            return packet;
        }
    }

    @Override
    public EzyPacket peek() throws InterruptedException {
        synchronized (this) {
            while (empty || processing) {
                wait();
            }
            processing = true;
            return queue.peek();
        }
    }

    @Override
    public EzyPacket peekNow() {
        synchronized (this) {
            if (empty || processing) {
                return null;
            }
            processing = true;
            return queue.peek();
        }
    }

    @Override
    public boolean isFull() {
        synchronized (this) {
            int size = queue.size();
            return size >= capacity;
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (this) {
            return queue.isEmpty();
        }
    }

    @Override
    public boolean add(EzyPacket packet) {
        synchronized (this) {
            int size = queue.size();
            if (size >= capacity) {
                return false;
            }
            queue.offer(packet);
            empty = false;
            if (!processing) {
                notifyAll();
            }
            return true;
        }
    }

    @Override
    public void again() {
        synchronized (this) {
            this.processing = false;
        }
    }

    @Override
    public void wakeup() {
        synchronized (this) {
            queue.offer(null);
            empty = false;
            processing = false;
            notifyAll();
        }
    }
}
