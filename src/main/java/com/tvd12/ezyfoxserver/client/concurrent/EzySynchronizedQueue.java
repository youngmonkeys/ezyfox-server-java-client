package com.tvd12.ezyfoxserver.client.concurrent;

import com.tvd12.ezyfoxserver.client.util.EzyQueue;

import java.util.List;

public class EzySynchronizedQueue<E> extends EzyQueue<E> {
    public EzySynchronizedQueue() {
        super();
    }

    public EzySynchronizedQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean add(E e) {
        synchronized (queue) {
            if (queue.size() >= capacity) {
                return false;
            }
            queue.offer(e);
        }
        return true;
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public E peek() {
        synchronized (queue) {
            return queue.peek();
        }
    }

    @Override
    public E poll() {
        synchronized (queue) {
            return queue.poll();
        }
    }

    @Override
    public void pollAll(List<E> list) {
        synchronized (queue) {
            while (queue.size() > 0) {
                list.add(queue.poll());
            }
        }
    }

    @Override
    public int size() {
        synchronized (queue) {
            return queue.size();
        }
    }

    @Override
    public void clear() {
        synchronized (queue) {
            queue.clear();
        }
    }
}