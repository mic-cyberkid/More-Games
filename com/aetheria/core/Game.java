package com.aetheria.core;

import com.aetheria.core.loop.FrameTimer;
import com.aetheria.core.loop.GameLoop;
import com.aetheria.input.Action;
import com.aetheria.input.ActionMap;
import com.aetheria.input.InputManager;
import com.aetheria.render.DebugOverlay;
import com.aetheria.render.Renderer;
import com.aetheria.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class Game extends JPanel {

    public static final int BASE_W = 320;
    public static final int BASE_H = 180;
    public static final int SCALE  = 4;

    private final BufferedImage internalBuffer =
        new BufferedImage(BASE_W, BASE_H, BufferedImage.TYPE_INT_ARGB);

    private final GameStateManager stateManager;
    private final InputManager     inputManager;
    private final ActionMap        actionMap;
    private final FrameTimer       frameTimer;
    private final DebugOverlay     debugOverlay;
    private final GameLoop         gameLoop;

    // Phase 2: Sprite Integration
    private com.aetheria.render.SpriteAnimator playerAnimator;

    public Game() {
        setPreferredSize(new Dimension(BASE_W * SCALE, BASE_H * SCALE));
        setBackground(Color.BLACK);
        setFocusable(true);

        this.stateManager = new GameStateManager();
        this.inputManager = new InputManager();
        this.actionMap    = new ActionMap(inputManager);
        this.frameTimer   = new FrameTimer();
        this.debugOverlay = new DebugOverlay();

        // Phase 2 initialization
        BufferedImage kaelenImg = com.aetheria.assets.AssetManager.get().getImage("/assets/sheets/player/kaelen_sheet.png");
        com.aetheria.render.SpriteSheet kaelenSheet = new com.aetheria.render.SpriteSheet(kaelenImg);
        this.playerAnimator = new com.aetheria.render.SpriteAnimator(kaelenSheet, 16, 16);
        playerAnimator.addAnimation("WALK_DOWN", 0, 3, 0.2, true);
        playerAnimator.play("WALK_DOWN");

        addKeyListener(inputManager);
        addMouseListener(inputManager);

        this.gameLoop = new GameLoop(this, stateManager, frameTimer);

        // Stub state for Phase 1
        stateManager.swap(new Screen() {
            @Override public void onEnter() {}
            @Override public void onExit() {}
            @Override public void onSuspend() {}
            @Override public void onResume() {}
            @Override public void update(double dt) {
                playerAnimator.update(dt);
                if (actionMap.isJustPressed(Action.DEBUG_TOGGLE)) {
                    debugOverlay.toggle();
                }
                if (actionMap.isJustPressed(Action.PAUSE)) {
                    Logger.info(Game.class, "Escape pressed - Toggle Pause (Stub)");
                }
                inputManager.endFrame();
            }
            @Override public void render(Renderer r, double alpha) {
                Graphics2D g = r.g();
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, BASE_W, BASE_H);

                BufferedImage playerFrame = playerAnimator.getCurrentFrame();
                if (playerFrame != null) {
                    g.drawImage(playerFrame, BASE_W / 2 - 8, BASE_H / 2 - 8, null);
                }

                g.setColor(Color.WHITE);
                g.drawString("Echoes of Aetheria - Phase 2 (Sprites)", 10, 20);
                debugOverlay.render(g, frameTimer);
            }
        });
    }

    public void start() {
        gameLoop.start();
    }

    public void render(double alpha) {
        synchronized (internalBuffer) {
            Graphics2D g = internalBuffer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.clearRect(0, 0, BASE_W, BASE_H);
            stateManager.render(new Renderer(g), alpha);
            g.dispose();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        synchronized (internalBuffer) {
            g2d.drawImage(internalBuffer, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
