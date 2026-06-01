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
        if (stack.isEmpty()) return;

        Object[] arr = stack.toArray();
        // 1. Find the bottom-most opaque screen
        int firstToRender = 0;
        for (int i = 0; i < arr.length; i++) {
            firstToRender = i;
            if (!((Screen) arr[i]).isTransparent()) break;
        }

        // 2. Render from that screen up to the top (which is index 0 in Deque.toArray())
        for (int i = firstToRender; i >= 0; i--) {
            ((Screen) arr[i]).render(renderer, alpha);
        }
    }
}
