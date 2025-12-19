package dev.yonel.telegrambot_spring_boot_starter.context;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton con acceso concurrente
 */
public class SessionManager {
    private static final ConcurrentHashMap<Long, UserSessionContext> sessions = new ConcurrentHashMap<>();

    public static UserSessionContext getContext(Long userid){
        return sessions.computeIfAbsent(userid, id -> new UserSessionContext());
    }
}
