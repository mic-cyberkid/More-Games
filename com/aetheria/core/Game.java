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

    // Phase 3 & 4: ECS and World
    private final com.aetheria.ecs.World world;
    private final com.aetheria.world.WorldMap worldMap;
    private final com.aetheria.render.Camera camera;
    private final com.aetheria.entity.Player player;

    // Phase 5: Story and Interaction
    private final com.aetheria.story.StoryFlags storyFlags;
    private final com.aetheria.story.QuestLog  questLog;
    private final com.aetheria.story.DialogueEngine dialogueEngine;
    private final com.aetheria.audio.AudioBus audioBus;
    private final com.aetheria.audio.AudioEngine audioEngine;
    private final com.aetheria.render.ParticleRenderer particleRenderer;
    private final com.aetheria.ui.HUD hud;

    // Phase 7: Save System
    private final com.aetheria.save.SaveManager saveManager;

    public Game() {
        setPreferredSize(new Dimension(BASE_W * SCALE, BASE_H * SCALE));
        setBackground(Color.BLACK);
        setFocusable(true);

        this.stateManager = new GameStateManager();
        this.inputManager = new InputManager();
        this.actionMap    = new ActionMap(inputManager);
        this.frameTimer   = new FrameTimer();
        this.debugOverlay = new DebugOverlay();

        // Phase 6 initialization
        this.audioBus = new com.aetheria.audio.AudioBus();
        this.audioEngine = new com.aetheria.audio.AudioEngine(audioBus);
        this.particleRenderer = new com.aetheria.render.ParticleRenderer();
        this.hud = new com.aetheria.ui.HUD();
        this.saveManager = new com.aetheria.save.SaveManager();

        com.aetheria.core.event.EventBus.get().subscribe(com.aetheria.core.event.events.MapTransitionEvent.class, e -> {
            loadMap(e.nextMapId());
        });

        // Phase 5 initialization
        this.storyFlags = new com.aetheria.story.StoryFlags();
        this.questLog = new com.aetheria.story.QuestLog();
        this.dialogueEngine = new com.aetheria.story.DialogueEngine();
        dialogueEngine.loadStub();

        com.aetheria.core.event.EventBus.get().subscribe(com.aetheria.core.event.events.DialogueStartedEvent.class, e -> {
            stateManager.push(new com.aetheria.story.DialogueScreen(stateManager, actionMap, dialogueEngine, storyFlags, e.dialogueId()));
        });

        // Phase 3 & 4 initialization
        this.world = new com.aetheria.ecs.World();
        this.worldMap = com.aetheria.world.MapLoader.loadStub(64, 64);
        this.camera = new com.aetheria.render.Camera(BASE_W, BASE_H);
        camera.setWorldBounds(worldMap.getWidth() * 16, worldMap.getHeight() * 16);

        world.addSystem(new com.aetheria.ecs.systems.MovementSystem(worldMap));
        world.addSystem(new com.aetheria.ecs.systems.AnimationSystem());
        world.addSystem(new com.aetheria.ecs.systems.ParticleSystem(particleRenderer));

        // Create player entity
        com.aetheria.ecs.Entity playerEnt = world.createEntity();
        world.getMapper(com.aetheria.ecs.components.TransformComponent.class).set(playerEnt.id(), new com.aetheria.ecs.components.TransformComponent(100, 100));
        world.getMapper(com.aetheria.ecs.components.VelocityComponent.class).set(playerEnt.id(), new com.aetheria.ecs.components.VelocityComponent(0, 0));
        world.getMapper(com.aetheria.ecs.components.CollisionComponent.class).set(playerEnt.id(), new com.aetheria.ecs.components.CollisionComponent(new com.aetheria.util.Rect(2, 8, 12, 8)));

        BufferedImage kaelenImg = com.aetheria.assets.AssetManager.get().getImage("/assets/sheets/player/kaelen_sheet.png");
        com.aetheria.render.SpriteSheet kaelenSheet = new com.aetheria.render.SpriteSheet(kaelenImg);
        com.aetheria.render.SpriteAnimator playerAnimator = new com.aetheria.render.SpriteAnimator(kaelenSheet, 16, 16);
        playerAnimator.addAnimation("WALK_DOWN", 0, 3, 0.2, true);
        playerAnimator.play("WALK_DOWN");
        world.getMapper(com.aetheria.ecs.components.SpriteComponent.class).set(playerEnt.id(), new com.aetheria.ecs.components.SpriteComponent(playerAnimator));

        this.player = new com.aetheria.entity.Player(playerEnt.id());
        world.addSystem(new com.aetheria.ecs.systems.InteractionSystem(playerEnt.id(), actionMap));

        // Create NPC Silas
        com.aetheria.ecs.Entity silas = world.createEntity();
        world.getMapper(com.aetheria.ecs.components.TransformComponent.class).set(silas.id(), new com.aetheria.ecs.components.TransformComponent(150, 100));
        world.getMapper(com.aetheria.ecs.components.InteractableComponent.class).set(silas.id(), new com.aetheria.ecs.components.InteractableComponent("START"));

        BufferedImage silasImg = com.aetheria.assets.AssetManager.get().getImage("/assets/sheets/player/kaelen_sheet.png"); // Placeholder
        com.aetheria.render.SpriteSheet silasSheet = new com.aetheria.render.SpriteSheet(silasImg);
        com.aetheria.render.SpriteAnimator silasAnim = new com.aetheria.render.SpriteAnimator(silasSheet, 16, 16);
        silasAnim.addAnimation("IDLE", 0, 1, 1.0, true);
        silasAnim.play("IDLE");
        world.getMapper(com.aetheria.ecs.components.SpriteComponent.class).set(silas.id(), new com.aetheria.ecs.components.SpriteComponent(silasAnim));

        addKeyListener(inputManager);
        addMouseListener(inputManager);

        this.gameLoop = new GameLoop(this, stateManager, frameTimer);

        // Main Menu state
        stateManager.swap(new com.aetheria.ui.MainMenu(
            stateManager,
            actionMap,
            () -> stateManager.swap(createWorldScreen()),
            () -> {
                com.aetheria.save.SaveData data = saveManager.load(1);
                if (data != null) {
                    applySaveData(data);
                    stateManager.swap(createWorldScreen());
                }
            }
        ));
    }

    private Screen createWorldScreen() {
        return new Screen() {
            @Override public void onEnter() {}
            @Override public void onExit() {}
            @Override public void onSuspend() {}
            @Override public void onResume() {}
            @Override public void update(double dt) {
                player.update(world, actionMap, dt);
                world.update(dt);

                var tc = world.getMapper(com.aetheria.ecs.components.TransformComponent.class).get(player.getEntityId());
                camera.follow(tc.x + 8, tc.y + 8);
                camera.update(dt);

                if (actionMap.isJustPressed(Action.DEBUG_TOGGLE)) {
                    debugOverlay.toggle();
                }
                if (actionMap.isJustPressed(Action.PAUSE)) {
                    stateManager.push(new com.aetheria.ui.PauseMenu(
                        stateManager,
                        actionMap,
                        () -> {
                            var tc = world.getMapper(com.aetheria.ecs.components.TransformComponent.class).get(player.getEntityId());
                            com.aetheria.save.SaveData data = new com.aetheria.save.SaveData(
                                1, 1, "wastes_start.amap", tc.x, tc.y,
                                storyFlags.getAllFlags(), questLog.getInventory()
                            );
                            saveManager.save(data);
                        },
                        () -> stateManager.swap(new com.aetheria.ui.MainMenu(
                            stateManager, actionMap,
                            () -> stateManager.swap(createWorldScreen()),
                            () -> {
                                com.aetheria.save.SaveData data = saveManager.load(1);
                                if (data != null) {
                                    applySaveData(data);
                                    stateManager.swap(createWorldScreen());
                                }
                            }
                        ))
                    ));
                }
                inputManager.endFrame();
            }
            @Override public void render(Renderer r, double alpha) {
                Graphics2D g = r.g();
                r.setCamera(camera);

                // 1. Clear background
                g.setColor(new Color(30, 30, 30));
                g.fillRect(0, 0, BASE_W, BASE_H);

                // 2. Render World (Camera Space)
                r.applyTransform();

                // Render Tiles (simplified)
                for (com.aetheria.world.TileLayer layer : worldMap.getLayers()) {
                    for (int y = 0; y < worldMap.getHeight(); y++) {
                        for (int x = 0; x < worldMap.getWidth(); x++) {
                            int tileId = layer.getTile(x, y);
                            if (tileId > 0) {
                                g.setColor(tileId == 2 ? Color.GRAY : Color.DARK_GRAY);
                                g.fillRect(x * 16, y * 16, 16, 16);
                            }
                        }
                    }
                }

                // Render Entities
                var transformMapper = world.getMapper(com.aetheria.ecs.components.TransformComponent.class);
                var spriteMapper = world.getMapper(com.aetheria.ecs.components.SpriteComponent.class);
                for (int id : world.getActiveEntities()) {
                    var tc = transformMapper.get(id);
                    var sc = spriteMapper.get(id);
                    if (tc != null && sc != null) {
                        BufferedImage frame = sc.animator.getCurrentFrame();
                        if (frame != null) g.drawImage(frame, (int)tc.x, (int)tc.y, null);
                    }
                }

                // Render Particles
                particleRenderer.render(g);

                // 3. Render UI (Screen Space)
                r.resetTransform();
                hud.render(g, 100, 100, questLog); // Stub HP
                g.setColor(Color.WHITE);
                g.drawString("Echoes of Aetheria - Phase 6 (Chapter 1)", 10, 20);
                debugOverlay.render(g, frameTimer);
            }
        });
    }

    private void loadMap(String mapId) {
        // Actual implementation would swap worldMap and reposition player
        Logger.info(Game.class, "Transitioning to map: " + mapId);
    }

    private void applySaveData(com.aetheria.save.SaveData data) {
        var tc = world.getMapper(com.aetheria.ecs.components.TransformComponent.class).get(player.getEntityId());
        tc.x = data.playerX();
        tc.y = data.playerY();
        storyFlags.clear();
        data.flags().forEach((k, v) -> {
            if (v instanceof Boolean b) storyFlags.setFlag(k, b);
            else if (v instanceof Integer i) storyFlags.setIntFlag(k, i);
        });
        questLog.getInventory().clear();
        questLog.getInventory().addAll(data.inventory());
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
