package com.aetheria.story;

import java.util.List;

public record DialogueNode(
    String id,
    String text,
    List<Choice> choices
) {
    public record Choice(String text, String targetNodeId, String conditionFlag, Object requiredValue, String actionFlag, Object actionValue) {}
}
