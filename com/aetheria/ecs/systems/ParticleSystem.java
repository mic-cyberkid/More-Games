package com.aetheria.ecs.systems;

import com.aetheria.ecs.World;
import com.aetheria.ecs.System;
import com.aetheria.render.ParticleRenderer;

public final class ParticleSystem implements System {
    private final ParticleRenderer renderer;

    public ParticleSystem(ParticleRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void update(World world, double dt) {
        renderer.update(dt);
    }
}
