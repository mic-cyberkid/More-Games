package com.aetheria.core.event.events;
import com.aetheria.core.event.Event;
public record PlayerHealthChangedEvent(int oldHp, int newHp, int maxHp) implements Event {}
