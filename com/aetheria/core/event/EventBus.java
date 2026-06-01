package com.aetheria.core.event;

import java.util.*;
import java.util.function.Consumer;

public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();
    public  static EventBus get() { return INSTANCE; }

    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> handlers
        = new HashMap<>();

    private EventBus() {}

    @SuppressWarnings("unchecked")
    public <E extends Event> void subscribe(Class<E> type, Consumer<E> handler) {
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    public <E extends Event> void unsubscribe(Class<E> type, Consumer<E> handler) {
        List<?> list = handlers.get(type);
        if (list != null) list.remove(handler);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void post(E event) {
        List<Consumer<? extends Event>> list = handlers.get(event.getClass());
        if (list == null) return;
        for (Consumer handler : new ArrayList<>(list)) {
            ((Consumer<E>) handler).accept(event);
        }
    }

    public void clearAll() { handlers.clear(); }
}
