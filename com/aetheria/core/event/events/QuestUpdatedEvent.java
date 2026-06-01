package com.aetheria.core.event.events;
import com.aetheria.core.event.Event;
public record QuestUpdatedEvent(String questId) implements Event {}
