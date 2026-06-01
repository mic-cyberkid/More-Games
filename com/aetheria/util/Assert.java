package com.aetheria.util;

public final class Assert {
    private static final boolean ENABLED = true; // Should be tied to dev/prod flag

    public static void that(boolean condition, String message) {
        if (ENABLED && !condition) {
            throw new AssertionError("ASSERTION FAILED: " + message);
        }
    }

    public static void notNull(Object obj, String message) {
        that(obj != null, message);
    }
}
