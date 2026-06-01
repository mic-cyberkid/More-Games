package com.aetheria.ecs.components;

import com.aetheria.ecs.Component;
import com.aetheria.render.SpriteAnimator;

public final class SpriteComponent implements Component {
    public SpriteAnimator animator;

    public SpriteComponent(SpriteAnimator animator) {
        this.animator = animator;
    }
}
