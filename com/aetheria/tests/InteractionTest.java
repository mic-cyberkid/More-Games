package com.aetheria.tests;

import com.aetheria.ecs.*;
import com.aetheria.ecs.components.*;
import com.aetheria.ecs.systems.*;
import com.aetheria.input.*;
import com.aetheria.util.Assert;
import com.aetheria.core.event.EventBus;
import com.aetheria.core.event.events.DialogueStartedEvent;
import java.awt.event.KeyEvent;

public class InteractionTest {
    private static boolean eventFired = false;

    public static void main(String[] args) {
        World world = new World();
        InputManager input = new InputManager();
        ActionMap actions = new ActionMap(input);

        EventBus.get().subscribe(DialogueStartedEvent.class, e -> {
            eventFired = true;
            Assert.that(e.dialogueId().equals("TEST_DLG"), "Wrong dialogue ID");
        });

        Entity player = world.createEntity();
        world.getMapper(TransformComponent.class).set(player.id(), new TransformComponent(0, 0));

        Entity npc = world.createEntity();
        world.getMapper(TransformComponent.class).set(npc.id(), new TransformComponent(10, 10));
        world.getMapper(InteractableComponent.class).set(npc.id(), new InteractableComponent("TEST_DLG"));

        InteractionSystem is = new InteractionSystem(player.id(), actions);

        // No key pressed
        is.update(world, 1.0);
        Assert.that(!eventFired, "Event should not fire without key");

        // Key pressed but out of range? (10,10) is dist ~14.1, range 24.
        // Use a dummy component that doesn't trigger HeadlessException if possible, or just mock the event.
        // Actually, InputManager doesn't care about the Component source for this test's purposes.
        KeyEvent ev = new KeyEvent(new java.awt.Canvas(), 0, 0, 0, KeyEvent.VK_E, 'e');
        input.keyPressed(ev);
        is.update(world, 1.0);
        Assert.that(eventFired, "Event should have fired");

        java.lang.System.out.println("InteractionTest PASSED");
    }
}
