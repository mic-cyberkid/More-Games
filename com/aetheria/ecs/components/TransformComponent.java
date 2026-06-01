package com.aetheria.ecs.components;

import com.aetheria.ecs.Component;

public final class TransformComponent implements Component {
    public float x, y;

    public TransformComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
