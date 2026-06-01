package com.aetheria.story;

import com.aetheria.core.Screen;
import com.aetheria.render.Renderer;
import com.aetheria.ui.DialogueBox;
import com.aetheria.input.Action;
import com.aetheria.input.ActionMap;
import com.aetheria.core.event.EventBus;
import com.aetheria.core.event.events.DialogueEndedEvent;
import com.aetheria.core.GameStateManager;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;

public final class DialogueScreen implements Screen {
    private final GameStateManager gsm;
    private final ActionMap actions;
    private final DialogueEngine engine;
    private final StoryFlags flags;
    private final DialogueBox box = new DialogueBox();

    private DialogueNode currentNode;
    private int selectedChoice = 0;
    private List<DialogueNode.Choice> availableChoices;

    public DialogueScreen(GameStateManager gsm, ActionMap actions, DialogueEngine engine, StoryFlags flags, String startNodeId) {
        this.gsm = gsm;
        this.actions = actions;
        this.engine = engine;
        this.flags = flags;
        setNode(startNodeId);
    }

    private void setNode(String id) {
        if ("END".equals(id)) {
            gsm.pop();
            EventBus.get().post(new DialogueEndedEvent());
            return;
        }
        currentNode = engine.getNode(id);
        box.setText(currentNode.text());
        updateChoices();
    }

    private void updateChoices() {
        availableChoices = currentNode.choices().stream()
            .filter(c -> c.conditionFlag() == null || Objects.equals(flags.get(c.conditionFlag()), c.requiredValue()))
            .toList();
        selectedChoice = 0;
    }

    @Override public void onEnter() {}
    @Override public void onExit() {}
    @Override public void onSuspend() {}
    @Override public void onResume() {}

    @Override
    public void update(double dt) {
        box.update(dt);

        if (actions.isJustPressed(Action.CONFIRM)) {
            if (!box.isFinished()) {
                box.skip();
            } else if (!availableChoices.isEmpty()) {
                DialogueNode.Choice choice = availableChoices.get(selectedChoice);
                if (choice.actionFlag() != null) {
                    flags.set(choice.actionFlag(), choice.actionValue());
                }
                setNode(choice.targetNodeId());
            }
        }

        if (box.isFinished() && !availableChoices.isEmpty()) {
            if (actions.isJustPressed(Action.MOVE_UP)) selectedChoice = (selectedChoice - 1 + availableChoices.size()) % availableChoices.size();
            if (actions.isJustPressed(Action.MOVE_DOWN)) selectedChoice = (selectedChoice + 1) % availableChoices.size();
        }
    }

    @Override
    public void render(Renderer r, double alpha) {
        Graphics2D g = r.g();
        box.render(g, 10, 120, 300, 50);

        if (box.isFinished() && !availableChoices.isEmpty()) {
            for (int i = 0; i < availableChoices.size(); i++) {
                g.drawString((i == selectedChoice ? "> " : "  ") + availableChoices.get(i).text(), 20, 135 + (i * 12));
            }
        }
    }

    @Override public boolean isTransparent() { return true; }
}
