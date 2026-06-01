package com.aetheria.tests;

import com.aetheria.core.event.Event;
import com.aetheria.core.event.EventBus;
import com.aetheria.core.event.events.PlayerHealthChangedEvent;
import com.aetheria.util.Assert;

public class EventBusTest {
    private static int callCount = 0;

    public static void main(String[] args) {
        EventBus bus = EventBus.get();

        bus.subscribe(PlayerHealthChangedEvent.class, e -> {
            callCount++;
            Assert.that(e.newHp() == 50, "HP should be 50");
        });

        bus.post(new PlayerHealthChangedEvent(100, 50, 100));

        Assert.that(callCount == 1, "Handler should have been called once");

        System.out.println("EventBusTest PASSED");
    }
}
