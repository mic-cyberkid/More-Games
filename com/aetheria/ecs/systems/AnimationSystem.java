package com.aetheria.ecs.systems;

import com.aetheria.ecs.World;
import com.aetheria.ecs.System;
import com.aetheria.ecs.components.SpriteComponent;

public final class AnimationSystem implements System {
    @Override
    public void update(World world, double dt) {
        var spriteMapper = world.getMapper(SpriteComponent.class);
        for (int id : world.getActiveEntities()) {
            SpriteComponent sc = spriteMapper.get(id);
            if (sc != null && sc.animator != null) {
                sc.animator.update(dt);
            }
        }
    }
}
