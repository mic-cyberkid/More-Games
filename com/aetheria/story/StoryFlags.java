package com.aetheria.story;

import java.util.HashMap;
import java.util.Map;

public final class StoryFlags {
    private final Map<String, Object> flags = new HashMap<>();

    public void set(String name, Object value) {
        flags.put(name, value);
    }

    public Object get(String name) {
        return flags.get(name);
    }

    public boolean isTrue(String name) {
        Object val = flags.get(name);
        return val instanceof Boolean && (Boolean) val;
    }

    public int getInt(String name) {
        Object val = flags.get(name);
        return val instanceof Integer ? (Integer) val : 0;
    }
}
