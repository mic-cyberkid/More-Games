package com.aetheria.core.event.events;
import com.aetheria.core.event.Event;
public record ItemPickedUpEvent(String itemId) implements Event {}
