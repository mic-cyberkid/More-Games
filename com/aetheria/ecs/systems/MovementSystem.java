package com.aetheria.ecs.systems;

import com.aetheria.ecs.World;
import com.aetheria.ecs.System;
import com.aetheria.ecs.components.TransformComponent;
import com.aetheria.ecs.components.VelocityComponent;
import com.aetheria.ecs.components.CollisionComponent;
import com.aetheria.world.WorldMap;

public final class MovementSystem implements System {
    private final WorldMap map;
    private final CollisionSystem collisionSystem = new CollisionSystem();
    private float friction = 0.85f;

    public MovementSystem(WorldMap map) {
        this.map = map;
    }

    @Override
    public void update(World world, double dt) {
        var transformMapper = world.getMapper(TransformComponent.class);
        var velocityMapper = world.getMapper(VelocityComponent.class);
        var collisionMapper = world.getMapper(CollisionComponent.class);

        for (int id : world.getActiveEntities()) {
            TransformComponent tc = transformMapper.get(id);
            VelocityComponent vc = velocityMapper.get(id);

            if (tc == null || vc == null) continue;

            float dx = vc.vx * (float) dt * 16; // Velocity in tiles/sec -> pixels/sec
            float dy = vc.vy * (float) dt * 16;

            if (collisionMapper.has(id)) {
                collisionSystem.resolve(world, id, dx, dy, map);
            } else {
                tc.x += dx;
                tc.y += dy;
            }

            // Apply friction
            vc.vx *= friction;
            vc.vy *= friction;
            if (Math.abs(vc.vx) < 0.01f) vc.vx = 0;
            if (Math.abs(vc.vy) < 0.01f) vc.vy = 0;
        }
    }
}
