package com.aetheria.core.event.events;
import com.aetheria.core.event.Event;
public record MapTransitionEvent(String nextMapId) implements Event {}
