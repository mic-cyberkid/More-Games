package com.aetheria.core.loop;

import com.aetheria.core.Game;
import com.aetheria.core.GameStateManager;
import com.aetheria.util.Logger;

public final class GameLoop {

    public static final double FIXED_TIMESTEP = 1.0 / 60.0;
    private static final double MAX_FRAME_TIME = 0.033;
    private static final long   NS_PER_SECOND  = 1_000_000_000L;

    private final Game              game;
    private final GameStateManager stateManager;
    private final FrameTimer        frameTimer;
    private volatile boolean        running = false;

    private long  previousTime;
    private double accumulator = 0.0;

    public GameLoop(Game game, GameStateManager stateManager, FrameTimer frameTimer) {
        this.game = game;
        this.stateManager = stateManager;
        this.frameTimer = frameTimer;
    }

    public void start() {
        running      = true;
        previousTime = System.nanoTime();

        Logger.info(GameLoop.class, "Starting game loop...");

        while (running) {
            long  currentTime  = System.nanoTime();
            double frameTime   = (currentTime - previousTime) / (double) NS_PER_SECOND;
            previousTime       = currentTime;

            frameTimer.tick();

            frameTime = Math.min(frameTime, MAX_FRAME_TIME);
            accumulator += frameTime;

            while (accumulator >= FIXED_TIMESTEP) {
                stateManager.update(FIXED_TIMESTEP);
                accumulator -= FIXED_TIMESTEP;
            }

            double alpha = accumulator / FIXED_TIMESTEP;
            game.render(alpha);

            // Frame pacing
            long   targetNs   = (long)(FIXED_TIMESTEP * NS_PER_SECOND);
            long   elapsedNs  = System.nanoTime() - currentTime;
            long   sleepNs    = targetNs - elapsedNs - 500_000L;
            if (sleepNs > 0) {
                try { Thread.sleep(sleepNs / 1_000_000L, (int)(sleepNs % 1_000_000L)); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    public void stop() { running = false; }
}
