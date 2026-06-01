package com.aetheria.ecs;

import com.aetheria.util.Assert;
import java.util.Arrays;

public final class ComponentMapper<T extends Component> {
    private final Class<T> type;
    private T[] components;

    @SuppressWarnings("unchecked")
    public ComponentMapper(Class<T> type, int initialCapacity) {
        this.type = type;
        this.components = (T[]) java.lang.reflect.Array.newInstance(type, initialCapacity);
    }

    public T get(int entityId) {
        if (entityId >= components.length) return null;
        return components[entityId];
    }

    public void set(int entityId, T component) {
        ensureCapacity(entityId);
        components[entityId] = component;
    }

    public void remove(int entityId) {
        if (entityId < components.length) {
            components[entityId] = null;
        }
    }

    public boolean has(int entityId) {
        return get(entityId) != null;
    }

    private void ensureCapacity(int entityId) {
        if (entityId >= components.length) {
            int newCapacity = Math.max(entityId + 1, components.length * 2);
            components = Arrays.copyOf(components, newCapacity);
        }
    }
}
