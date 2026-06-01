package com.aetheria.core.event.events;
import com.aetheria.core.event.Event;
public record FlagChangedEvent(String flagName, Object newValue) implements Event {}
