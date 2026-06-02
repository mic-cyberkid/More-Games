package com.aetheria.ui;

import com.aetheria.core.Screen;
import com.aetheria.render.Renderer;
import com.aetheria.input.Action;
import com.aetheria.input.ActionMap;
import com.aetheria.core.GameStateManager;
import java.awt.*;

public final class PauseMenu implements Screen {
    private final GameStateManager gsm;
    private final ActionMap actions;
    private final Runnable onSave;
    private final Runnable onQuit;
    private int selected = 0;
    private final String[] options = {"Resume", "Save", "Quit to Menu"};

    public PauseMenu(GameStateManager gsm, ActionMap actions, Runnable onSave, Runnable onQuit) {
        this.gsm = gsm;
        this.actions = actions;
        this.onSave = onSave;
        this.onQuit = onQuit;
    }

    @Override public void onEnter() {}
    @Override public void onExit() {}
    @Override public void onSuspend() {}
    @Override public void onResume() {}

    @Override
    public void update(double dt) {
        if (actions.isJustPressed(Action.PAUSE)) gsm.pop();
        if (actions.isJustPressed(Action.MOVE_UP)) selected = (selected - 1 + options.length) % options.length;
        if (actions.isJustPressed(Action.MOVE_DOWN)) selected = (selected + 1) % options.length;
        if (actions.isJustPressed(Action.CONFIRM)) {
            if (selected == 0) gsm.pop();
            if (selected == 1) onSave.run();
            if (selected == 2) onQuit.run();
        }
    }

    @Override
    public void render(Renderer r, double alpha) {
        Graphics2D g = r.g();
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(80, 40, 160, 100);
        g.setColor(Color.WHITE);
        g.drawRect(80, 40, 160, 100);
        for (int i = 0; i < options.length; i++) {
            g.drawString((i == selected ? "> " : "  ") + options[i], 110, 70 + i * 20);
        }
    }

    @Override public boolean isTransparent() { return true; }
}
