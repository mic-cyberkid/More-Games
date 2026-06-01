package com.aetheria.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final boolean DEBUG_ENABLED = true; // Could be controlled by a build flag

    public static void debug(Class<?> clazz, String message) {
        if (DEBUG_ENABLED) log(Level.DEBUG, clazz, message);
    }

    public static void info(Class<?> clazz, String message) {
        log(Level.INFO, clazz, message);
    }

    public static void warn(Class<?> clazz, String message) {
        log(Level.WARN, clazz, message);
    }

    public static void error(Class<?> clazz, String message, Throwable t) {
        log(Level.ERROR, clazz, message + (t != null ? " | " + t.getMessage() : ""));
        if (t != null) t.printStackTrace();
    }

    private static void log(Level level, Class<?> clazz, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String threadName = Thread.currentThread().getName();
        String output = String.format("%s [%s] %s %s - %s",
            timestamp, threadName, level, clazz.getSimpleName(), message);

        if (level == Level.ERROR || level == Level.WARN) {
            System.err.println(output);
        } else {
            System.out.println(output);
        }
    }
}
