package com.aetheria.world;

import java.util.ArrayList;
import java.util.List;
import com.aetheria.util.Rect;

public final class ObjectLayer {
    public record MapTrigger(Rect bounds, WorldTransition transition) {}

    private final List<MapTrigger> triggers = new ArrayList<>();

    public void addTrigger(MapTrigger trigger) {
        triggers.add(trigger);
    }

    public List<MapTrigger> getTriggers() {
        return triggers;
    }
}
