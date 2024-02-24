package com.tvd12.ezyfoxserver.client.socket;

public interface EzyPacketQueue {

    int size();

    void clear();

    EzyPacket take();

    EzyPacket peek() throws InterruptedException;

    EzyPacket peekNow();

    boolean isFull();

    boolean isEmpty();

    boolean add(EzyPacket packet);

    void again();

    void wakeup();
}
