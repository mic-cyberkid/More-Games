package com.aetheria.ui;

import com.aetheria.core.Screen;
import com.aetheria.render.Renderer;
import com.aetheria.input.Action;
import com.aetheria.input.ActionMap;
import com.aetheria.core.GameStateManager;
import java.awt.*;

public final class MainMenu implements Screen {
    private final GameStateManager gsm;
    private final ActionMap actions;
    private int selected = 0;
    private final String[] options = {"New Game", "Load Game", "Quit"};

    public MainMenu(GameStateManager gsm, ActionMap actions) {
        this.gsm = gsm;
        this.actions = actions;
    }

    @Override public void onEnter() {}
    @Override public void onExit() {}
    @Override public void onSuspend() {}
    @Override public void onResume() {}

    @Override
    public void update(double dt) {
        if (actions.isJustPressed(Action.MOVE_UP)) selected = (selected - 1 + options.length) % options.length;
        if (actions.isJustPressed(Action.MOVE_DOWN)) selected = (selected + 1) % options.length;
        if (actions.isJustPressed(Action.CONFIRM)) {
            if (selected == 0) { /* New Game logic handled in Game.java hook */ }
            if (selected == 2) System.exit(0);
        }
    }

    @Override
    public void render(Renderer r, double alpha) {
        Graphics2D g = r.g();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 320, 180);
        g.setColor(Color.WHITE);
        g.drawString("ECHOES OF AETHERIA", 100, 50);
        for (int i = 0; i < options.length; i++) {
            g.drawString((i == selected ? "> " : "  ") + options[i], 120, 80 + i * 20);
        }
    }
}
