package com.aetheria.ecs.components;

import com.aetheria.ecs.Component;

public final class StoryFlagComponent implements Component {
    public String flagName;
    public Object requiredValue;

    public StoryFlagComponent(String flagName, Object requiredValue) {
        this.flagName = flagName;
        this.requiredValue = requiredValue;
    }
}
