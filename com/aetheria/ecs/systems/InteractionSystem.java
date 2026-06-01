package com.aetheria.ecs.systems;

import com.aetheria.ecs.World;
import com.aetheria.ecs.System;
import com.aetheria.ecs.components.InteractableComponent;
import com.aetheria.ecs.components.TransformComponent;
import com.aetheria.input.Action;
import com.aetheria.input.ActionMap;
import com.aetheria.core.event.EventBus;
import com.aetheria.core.event.events.DialogueStartedEvent;

public final class InteractionSystem implements System {
    private final int playerEntityId;
    private final ActionMap actions;

    public InteractionSystem(int playerEntityId, ActionMap actions) {
        this.playerEntityId = playerEntityId;
        this.actions = actions;
    }

    @Override
    public void update(World world, double dt) {
        if (!actions.isJustPressed(Action.INTERACT)) return;

        var transformMapper = world.getMapper(TransformComponent.class);
        var interactMapper = world.getMapper(InteractableComponent.class);

        TransformComponent pt = transformMapper.get(playerEntityId);
        if (pt == null) return;

        for (int id : world.getActiveEntities()) {
            if (id == playerEntityId) continue;

            TransformComponent tt = transformMapper.get(id);
            InteractableComponent ic = interactMapper.get(id);

            if (tt != null && ic != null) {
                float dist = (float) Math.sqrt(Math.pow(pt.x - tt.x, 2) + Math.pow(pt.y - tt.y, 2));
                if (dist <= ic.range) {
                    EventBus.get().post(new DialogueStartedEvent(ic.dialogueId));
                    break;
                }
            }
        }
    }
}
