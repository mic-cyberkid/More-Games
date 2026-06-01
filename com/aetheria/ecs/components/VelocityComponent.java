package com.aetheria.ecs.components;

import com.aetheria.ecs.Component;

public final class VelocityComponent implements Component {
    public float vx, vy;

    public VelocityComponent(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }
}
