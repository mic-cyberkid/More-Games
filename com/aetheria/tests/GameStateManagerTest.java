package com.aetheria.tests;

import com.aetheria.core.GameStateManager;
import com.aetheria.core.Screen;
import com.aetheria.render.Renderer;
import com.aetheria.util.Assert;

public class GameStateManagerTest {
    private static int enterCount = 0;
    private static int exitCount = 0;

    public static void main(String[] args) {
        GameStateManager gsm = new GameStateManager();

        Screen s1 = new Screen() {
            @Override public void onEnter() { enterCount++; }
            @Override public void onExit() { exitCount++; }
            @Override public void onSuspend() {}
            @Override public void onResume() {}
            @Override public void update(double dt) {}
            @Override public void render(Renderer r, double alpha) {}
        };

        gsm.swap(s1);
        Assert.that(enterCount == 1, "Enter count should be 1");

        gsm.pop();
        Assert.that(exitCount == 1, "Exit count should be 1");

        System.out.println("GameStateManagerTest PASSED");
    }
}
