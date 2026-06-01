package com.aetheria.tests;

import com.aetheria.core.event.EventBus;
import com.aetheria.core.event.events.MapTransitionEvent;
import com.aetheria.ecs.World;
import com.aetheria.ecs.components.*;
import com.aetheria.ecs.systems.CollisionSystem;
import com.aetheria.world.*;
import com.aetheria.util.Assert;
import com.aetheria.util.Rect;

public class Chapter1Test {
    private static String transitionedTo = null;

    public static void main(String[] args) {
        World world = new World();
        WorldMap map = new WorldMap(10, 10);
        map.addLayer(new TileLayer(10, 10));

        // Add trigger at (100, 100)
        map.getObjectLayer().addTrigger(new ObjectLayer.MapTrigger(
            new Rect(100, 100, 32, 32),
            new WorldTransition("next_map.amap", 0, 0)
        ));

        EventBus.get().subscribe(MapTransitionEvent.class, e -> transitionedTo = e.nextMapId());

        CollisionSystem cs = new CollisionSystem();
        int player = world.createEntity().id();
        world.getMapper(TransformComponent.class).set(player, new TransformComponent(90, 90));
        world.getMapper(CollisionComponent.class).set(player, new CollisionComponent(new Rect(0, 0, 16, 16)));

        // Move into trigger
        cs.resolve(world, player, 15, 15, map);

        Assert.that("next_map.amap".equals(transitionedTo), "Transition event should have fired. Target: " + transitionedTo);

        java.lang.System.out.println("Chapter1Test PASSED");
    }
}
