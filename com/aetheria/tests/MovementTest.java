package com.aetheria.tests;

import com.aetheria.ecs.*;
import com.aetheria.ecs.components.*;
import com.aetheria.ecs.systems.*;
import com.aetheria.world.*;
import com.aetheria.util.Assert;
import com.aetheria.util.Rect;

public class MovementTest {
    public static void main(String[] args) {
        World world = new World();
        WorldMap map = new WorldMap(10, 10);
        TileLayer layer = new TileLayer(10, 10);
        layer.setTile(5, 5, 2); // WALL at (5, 5) which is (80, 80) to (95, 95)
        map.addLayer(layer);

        MovementSystem ms = new MovementSystem(map);

        Entity e = world.createEntity();
        TransformComponent tc = new TransformComponent(64, 80);
        VelocityComponent vc = new VelocityComponent(1, 0); // Moving Right
        CollisionComponent cc = new CollisionComponent(new Rect(0, 0, 16, 16));

        world.getMapper(TransformComponent.class).set(e.id(), tc);
        world.getMapper(VelocityComponent.class).set(e.id(), vc);
        world.getMapper(CollisionComponent.class).set(e.id(), cc);

        // Update movement
        ms.update(world, 1.0); // Move 16 pixels

        Assert.that(tc.x < 80, "Should be stopped by wall at x=80. Current X: " + tc.x);

        java.lang.System.out.println("MovementTest PASSED");
    }
}
