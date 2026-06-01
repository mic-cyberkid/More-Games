package com.aetheria.story;

import java.util.ArrayList;
import java.util.List;

public final class QuestLog {
    public record Quest(String id, String title, String description, boolean completed) {}

    private final List<Quest> activeQuests = new ArrayList<>();
    private final List<Quest> completedQuests = new ArrayList<>();

    public void addQuest(Quest quest) {
        activeQuests.add(quest);
    }

    public void completeQuest(String id) {
        activeQuests.removeIf(q -> {
            if (q.id().equals(id)) {
                completedQuests.add(new Quest(q.id(), q.title(), q.description(), true));
                return true;
            }
            return false;
        });
    }

    public List<Quest> getActiveQuests() { return activeQuests; }
    public List<Quest> getCompletedQuests() { return completedQuests; }
}
