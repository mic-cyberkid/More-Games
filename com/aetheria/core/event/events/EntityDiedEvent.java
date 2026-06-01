package com.aetheria.core.event.events;
import com.aetheria.core.event.Event;
public record EntityDiedEvent(int entityId) implements Event {}
