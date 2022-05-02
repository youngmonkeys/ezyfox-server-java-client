package com.tvd12.ezyfoxserver.client.constant;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.constant.EzyConstant;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum EzyCommand implements EzyConstant {

    ERROR(10, 10),
    HANDSHAKE(11, 0),
    PING(12, 10),
    PONG(13, 10),
    DISCONNECT(14, 10),
    LOGIN(20, 1),
    LOGIN_ERROR(21, 10),
    APP_ACCESS(30, 2),
    APP_REQUEST(31, 10),
    APP_EXIT(33, 10),
    APP_ACCESS_ERROR(34, 10),
    APP_REQUEST_ERROR(35, 10),
    PLUGIN_INFO(40, 10),
    PLUGIN_REQUEST(41, 10),
    UDP_HANDSHAKE(50, 10);

    private final int id;
    private final int priority;

    private static final Set<EzyCommand> SYSTEM_COMMANDS = systemCommands();
    private static final Map<Integer, EzyCommand> COMMANDS_BY_ID = commandsById();

    EzyCommand(int id, int priority) {
        this.id = id;
        this.priority = priority;
    }

    public static EzyCommand valueOf(int id) {
        return COMMANDS_BY_ID.get(id);
    }

    private static Set<EzyCommand> systemCommands() {
        return Sets.newHashSet(HANDSHAKE, LOGIN, APP_ACCESS, APP_EXIT, DISCONNECT);
    }

    private static Map<Integer, EzyCommand> commandsById() {
        Map<Integer, EzyCommand> map = new ConcurrentHashMap<>();
        for (EzyCommand cmd : values()) {
            map.put(cmd.getId(), cmd);
        }
        return map;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isSystemCommand() {
        return SYSTEM_COMMANDS.contains(this);
    }

    public int compareByPriority(EzyCommand other) {
        return this.getPriority() - other.getPriority();
    }

    @Override
    public String getName() {
        return toString();
    }
}
