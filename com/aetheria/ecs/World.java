package com.aetheria.ecs;

import java.util.*;

public final class World {
    private int nextEntityId = 0;
    private final Set<Integer> activeEntities = new HashSet<>();
    private final Map<Class<? extends Component>, ComponentMapper<?>> mappers = new HashMap<>();
    private final List<System> systems = new ArrayList<>();

    public Entity createEntity() {
        int id = nextEntityId++;
        activeEntities.add(id);
        return new Entity(id);
    }

    public void destroyEntity(int id) {
        activeEntities.remove(id);
        for (ComponentMapper<?> mapper : mappers.values()) {
            mapper.remove(id);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> ComponentMapper<T> getMapper(Class<T> type) {
        return (ComponentMapper<T>) mappers.computeIfAbsent(type, k -> new ComponentMapper<>(type, 1024));
    }

    public void addSystem(System system) {
        systems.add(system);
    }

    public void update(double dt) {
        for (System system : systems) {
            system.update(this, dt);
        }
    }

    public Set<Integer> getActiveEntities() {
        return Collections.unmodifiableSet(activeEntities);
    }
}
