package com.aetheria.tests;

import com.aetheria.ecs.*;
import com.aetheria.ecs.components.TransformComponent;
import com.aetheria.util.Assert;

public class ECSTest {
    public static void main(String[] args) {
        World world = new World();
        ComponentMapper<TransformComponent> mapper = world.getMapper(TransformComponent.class);

        long start = java.lang.System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            Entity e = world.createEntity();
            mapper.set(e.id(), new TransformComponent(i, i));
        }
        long end = java.lang.System.currentTimeMillis();

        java.lang.System.out.println("Created 5000 entities in " + (end - start) + "ms");
        Assert.that(world.getActiveEntities().size() == 5000, "Active entities should be 5000");

        // Benchmark lookup
        start = java.lang.System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            TransformComponent t = mapper.get(i);
            Assert.notNull(t, "Transform should not be null");
        }
        end = java.lang.System.currentTimeMillis();
        java.lang.System.out.println("Looked up 5000 components in " + (end - start) + "ms");

        java.lang.System.out.println("ECSTest PASSED");
    }
}
