package com.aetheria.tests;

import com.aetheria.story.*;
import com.aetheria.util.Assert;
import java.util.List;

public class DialogueTest {
    public static void main(String[] args) {
        DialogueEngine engine = new DialogueEngine();
        engine.loadStub();

        DialogueNode start = engine.getNode("START");
        Assert.notNull(start, "Start node should exist");
        Assert.that(start.choices().size() == 2, "Start should have 2 choices");

        StoryFlags flags = new StoryFlags();
        // Choice 2 requires HAS_PENDANT
        long availableCount = start.choices().stream()
            .filter(c -> c.conditionFlag() == null || java.util.Objects.equals(flags.get(c.conditionFlag()), c.requiredValue()))
            .count();
        Assert.that(availableCount == 1, "Only 1 choice should be available initially");

        flags.set("HAS_PENDANT", true);
        availableCount = start.choices().stream()
            .filter(c -> c.conditionFlag() == null || java.util.Objects.equals(flags.get(c.conditionFlag()), c.requiredValue()))
            .count();
        Assert.that(availableCount == 2, "Both choices should be available with pendant");

        java.lang.System.out.println("DialogueTest PASSED");
    }
}
