package com.aetheria.ecs.components;

import com.aetheria.ecs.Component;

public final class HealthComponent implements Component {
    public int hp;
    public int maxHp;

    public HealthComponent(int hp, int maxHp) {
        this.hp = hp;
        this.maxHp = maxHp;
    }
}
