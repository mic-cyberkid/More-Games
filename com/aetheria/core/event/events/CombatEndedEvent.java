package com.aetheria.core.event.events;
import com.aetheria.core.event.Event;
public record CombatEndedEvent(boolean victory) implements Event {}
