package com.aetheria.core;

import com.aetheria.render.Renderer;
import com.aetheria.util.Logger;
import java.util.ArrayDeque;
import java.util.Deque;

public final class GameStateManager {

    private final Deque<Screen> stack = new ArrayDeque<>();

    public void swap(Screen newScreen) {
        Logger.info(GameStateManager.class, "Swapping state to: " + newScreen.getClass().getSimpleName());
        if (!stack.isEmpty()) stack.peek().onExit();
        stack.clear();
        stack.push(newScreen);
        newScreen.onEnter();
    }

    public void push(Screen overlay) {
        Logger.info(GameStateManager.class, "Pushing state: " + overlay.getClass().getSimpleName());
        if (!stack.isEmpty()) stack.peek().onSuspend();
        stack.push(overlay);
        overlay.onEnter();
    }

    public void pop() {
        if (stack.isEmpty()) return;
        Logger.info(GameStateManager.class, "Popping state: " + stack.peek().getClass().getSimpleName());
        stack.pop().onExit();
        if (!stack.isEmpty()) {
            Logger.info(GameStateManager.class, "Resuming state: " + stack.peek().getClass().getSimpleName());
            stack.peek().onResume();
        }
    }

    public void update(double dt) {
        if (!stack.isEmpty()) stack.peek().update(dt);
    }

    public void render(Renderer renderer, double alpha) {
        Object[] arr = stack.toArray();
        for (int i = arr.length - 1; i >= 0; i--) {
            Screen s = (Screen) arr[i];
            s.render(renderer, alpha);
            if (!s.isTransparent()) break;
        }
    }
}
