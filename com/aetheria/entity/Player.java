package com.aetheria.entity;

import com.aetheria.ecs.World;
import com.aetheria.ecs.components.VelocityComponent;
import com.aetheria.input.Action;
import com.aetheria.input.ActionMap;

public final class Player {
    private final int entityId;
    private float walkSpeed = 6.0f;
    private float runSpeed = 10.0f;

    public Player(int entityId) {
        this.entityId = entityId;
    }

    public void update(World world, ActionMap actions, double dt) {
        var velocityMapper = world.getMapper(VelocityComponent.class);
        VelocityComponent vc = velocityMapper.get(entityId);
        if (vc == null) return;

        float speed = actions.isHeld(Action.RUN) ? runSpeed : walkSpeed;
        float moveX = 0, moveY = 0;

        if (actions.isHeld(Action.MOVE_UP))    moveY -= 1;
        if (actions.isHeld(Action.MOVE_DOWN))  moveY += 1;
        if (actions.isHeld(Action.MOVE_LEFT))  moveX -= 1;
        if (actions.isHeld(Action.MOVE_RIGHT)) moveX += 1;

        if (moveX != 0 || moveY != 0) {
            // Normalize
            float length = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            vc.vx += (moveX / length) * speed * dt * 20; // Acceleration
            vc.vy += (moveY / length) * speed * dt * 20;
        }
    }

    public int getEntityId() { return entityId; }
}
