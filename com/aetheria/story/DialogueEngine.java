package com.aetheria.story;

import java.util.*;

public final class DialogueEngine {
    private final Map<String, DialogueNode> nodes = new HashMap<>();

    public void addNode(DialogueNode node) {
        nodes.put(node.id(), node);
    }

    public DialogueNode getNode(String id) {
        return nodes.get(id);
    }

    // Stub for actual .adlg parsing
    public void loadStub() {
        addNode(new DialogueNode("START", "Welcome to the Wastes, boy. Found anything good?", List.of(
            new DialogueNode.Choice("Just junk, Silas.", "JUNK_RESPONSE", null, null, null, null),
            new DialogueNode.Choice("Found a pendant.", "PENDANT_RESPONSE", "HAS_PENDANT", true, null, null)
        )));

        addNode(new DialogueNode("JUNK_RESPONSE", "Always junk. World's full of it.", List.of(
            new DialogueNode.Choice("Leave", "END", null, null, "SILAS_MET", true)
        )));

        addNode(new DialogueNode("PENDANT_RESPONSE", "That... that was Lyra's. Where did you find it?", List.of(
            new DialogueNode.Choice("In the ruins.", "END", null, null, "SILAS_MET", true)
        )));
    }
}
