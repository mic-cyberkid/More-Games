package com.aetheria.ecs.components;

import com.aetheria.ecs.Component;

public final class InteractableComponent implements Component {
    public String dialogueId;
    public float range = 24.0f;

    public InteractableComponent(String dialogueId) {
        this.dialogueId = dialogueId;
    }
}
