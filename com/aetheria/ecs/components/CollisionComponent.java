package com.aetheria.ecs.components;

import com.aetheria.ecs.Component;
import com.aetheria.util.Rect;

public final class CollisionComponent implements Component {
    public Rect bounds;
    public boolean solid = true;

    public CollisionComponent(Rect bounds) {
        this.bounds = bounds;
    }
}
