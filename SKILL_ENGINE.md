# SKILL_ENGINE.md — Core Engine Implementation Guide
# Covers: Game Loop, FSM, Event Bus, Input System, Frame Timing

---

## §1 — GAME LOOP: FIXED TIMESTEP WITH VARIABLE RENDERING

### Why Fixed Timestep?
Physics, collision, AI, and pathfinding are **deterministic only at fixed dt**. Variable dt
produces non-reproducible bugs (different speeds on different machines). The canonical solution
is a fixed-update accumulator with interpolated rendering.

### The Correct Algorithm (Gaffer's "Fix Your Timestep")

```java
// GameLoop.java
package com.aetheria.core.loop;

public final class GameLoop {

    // FIXED — never change this at runtime
    public static final double FIXED_TIMESTEP = 1.0 / 60.0;      // 60 Hz logic
    private static final double MAX_FRAME_TIME = 0.033;            // 30 FPS floor (prevents spiral)
    private static final long   NS_PER_SECOND  = 1_000_000_000L;

    private final GameStateManager stateManager;
    private final Renderer          renderer;
    private volatile boolean        running = false;

    private long  previousTime;
    private double accumulator = 0.0;

    public void start() {
        running      = true;
        previousTime = System.nanoTime();

        while (running) {
            long  currentTime  = System.nanoTime();
            double frameTime   = (currentTime - previousTime) / (double) NS_PER_SECOND;
            previousTime       = currentTime;

            // Clamp: prevents "spiral of death" on slow machines
            frameTime = Math.min(frameTime, MAX_FRAME_TIME);

            accumulator += frameTime;

            // --- FIXED UPDATE ---
            while (accumulator >= FIXED_TIMESTEP) {
                stateManager.update(FIXED_TIMESTEP);   // ALL game logic here
                accumulator -= FIXED_TIMESTEP;
            }

            // --- VARIABLE RENDER ---
            double alpha = accumulator / FIXED_TIMESTEP; // interpolation factor [0,1]
            stateManager.render(renderer, alpha);

            // --- FRAME PACING ---
            long   targetNs   = (long)(FIXED_TIMESTEP * NS_PER_SECOND);
            long   elapsedNs  = System.nanoTime() - currentTime;
            long   sleepNs    = targetNs - elapsedNs - 500_000L; // 0.5ms margin
            if (sleepNs > 0) {
                try { Thread.sleep(sleepNs / 1_000_000L, (int)(sleepNs % 1_000_000L)); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    public void stop() { running = false; }
}
```

### Key Rules
- **`update(double dt)`** receives exactly `FIXED_TIMESTEP` every call — never a variable
- **`render(Renderer, double alpha)`** uses `alpha` to interpolate visual positions between
  the previous and current logical positions (prevents jitter at high FPS)
- **`MAX_FRAME_TIME`** prevents the spiral of death on garbage collection pauses or window drag
- Never call `Thread.sleep(1)` as it sleeps 1-15ms on Windows; use the nanosecond form

---

## §2 — GAME STATE MACHINE (PUSH-POP FSM)

### Why a Stack, Not a Simple State Variable?
Combat overlays World. Pause overlays Combat. Dialogue overlays World. A stack handles
all of this naturally without explicit tracking of "previous state."

```java
// GameStateManager.java
package com.aetheria.core;

import java.util.ArrayDeque;
import java.util.Deque;

public final class GameStateManager {

    private final Deque<Screen> stack = new ArrayDeque<>();

    /** Replace entire stack with a new root state (e.g., load game, new game). */
    public void swap(Screen newScreen) {
        if (!stack.isEmpty()) stack.peek().onExit();
        stack.clear();
        stack.push(newScreen);
        newScreen.onEnter();
    }

    /** Push an overlay (e.g., pause menu, dialogue). Current state is suspended, not exited. */
    public void push(Screen overlay) {
        if (!stack.isEmpty()) stack.peek().onSuspend();
        stack.push(overlay);
        overlay.onEnter();
    }

    /** Remove top overlay. Resumes the state below. */
    public void pop() {
        if (stack.isEmpty()) return;
        stack.pop().onExit();
        if (!stack.isEmpty()) stack.peek().onResume();
    }

    /** Update only the TOP screen's logic. */
    public void update(double dt) {
        if (!stack.isEmpty()) stack.peek().update(dt);
    }

    /**
     * Render all screens that are marked as transparent
     * (so world is visible behind pause menu).
     */
    public void render(Renderer renderer, double alpha) {
        // Render from bottom to top; skip non-transparent lower layers
        Object[] arr = stack.toArray();
        for (int i = arr.length - 1; i >= 0; i--) {
            Screen s = (Screen) arr[i];
            s.render(renderer, alpha);
            if (!s.isTransparent()) break; // Don't render further down
        }
    }
}
```

### Screen Interface

```java
// Screen.java
package com.aetheria.core;

public interface Screen {
    void   onEnter();         // called when pushed or swapped to
    void   onExit();          // called when removed from stack
    void   onSuspend();       // called when another screen is pushed on top
    void   onResume();        // called when top screen is popped
    void   update(double dt); // fixed dt in seconds
    void   render(Renderer r, double alpha); // alpha for interpolation
    /**
     * If true, screens below this one in the stack are also rendered.
     * Use for: pause menus, dialogue boxes, inventory overlays.
     */
    default boolean isTransparent() { return false; }
}
```

---

## §3 — EVENT BUS: TYPED PUB/SUB

### Design Constraints
- No reflection (hot path violation)
- Type-safe without casting
- Synchronous delivery (immediate, on the main thread)
- Support wildcard subscribe to all events (for logging/debug)

```java
// Event.java — sealed so all events are known at compile time
package com.aetheria.core.event;

public sealed interface Event permits
    PlayerHealthChangedEvent,
    EntityDiedEvent,
    MapTransitionEvent,
    DialogueStartedEvent,
    DialogueEndedEvent,
    QuestUpdatedEvent,
    ItemPickedUpEvent,
    FlagChangedEvent,
    CombatStartedEvent,
    CombatEndedEvent {
    // No methods required; marker interface
}

// EventBus.java
package com.aetheria.core.event;

import java.util.*;
import java.util.function.Consumer;

public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();
    public  static EventBus get() { return INSTANCE; }

    // Map from event class → list of handlers
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> handlers
        = new HashMap<>();

    private EventBus() {}

    @SuppressWarnings("unchecked")
    public <E extends Event> void subscribe(Class<E> type, Consumer<E> handler) {
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    public <E extends Event> void unsubscribe(Class<E> type, Consumer<E> handler) {
        List<?> list = handlers.get(type);
        if (list != null) list.remove(handler);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void post(E event) {
        List<Consumer<? extends Event>> list = handlers.get(event.getClass());
        if (list == null) return;
        // Iterate a copy to allow unsubscribe inside handlers
        for (Consumer handler : new ArrayList<>(list)) {
            ((Consumer<E>) handler).accept(event);
        }
    }

    /** Remove all subscriptions for a screen when it exits (prevents memory leaks). */
    public void clearAll() { handlers.clear(); }
}
```

### Example Event Definition

```java
// PlayerHealthChangedEvent.java
package com.aetheria.core.event.events;

public record PlayerHealthChangedEvent(int oldHp, int newHp, int maxHp) implements Event {}
```

### Usage Pattern

```java
// In a Screen's onEnter():
EventBus.get().subscribe(PlayerHealthChangedEvent.class, e -> {
    hud.updateHealthBar(e.newHp(), e.maxHp());
});

// In a System's update():
EventBus.get().post(new PlayerHealthChangedEvent(oldHp, newHp, maxHp));
```

**Rule:** Always unsubscribe in `onExit()`. Use a local list of subscriptions if needed:
```java
private final List<Runnable> unsubscribers = new ArrayList<>();
// In onEnter():
unsubscribers.add(() -> EventBus.get().unsubscribe(PlayerHealthChangedEvent.class, handler));
// In onExit():
unsubscribers.forEach(Runnable::run);
unsubscribers.clear();
```

---

## §4 — INPUT SYSTEM

### Design: Separate Raw State from Game Actions

The `InputManager` is **game-logic ignorant**. It records raw key/mouse state.
The `ActionMap` translates raw keys to typed `Action` values.
Game code reads `ActionMap`, never raw key codes.

```java
// Action.java — all possible game inputs
package com.aetheria.input;

public enum Action {
    MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT,
    RUN, INTERACT, ATTACK,
    OPEN_INVENTORY, PAUSE,
    CONFIRM, CANCEL,
    DEBUG_TOGGLE,
    // Combat-specific
    COMBAT_ACTION_1, COMBAT_ACTION_2, COMBAT_ACTION_3, COMBAT_ACTION_4
}

// InputManager.java
package com.aetheria.input;

import java.awt.event.*;
import java.util.BitSet;

public final class InputManager implements KeyListener, MouseListener {

    private static final int KEY_COUNT   = 256;
    private static final int MOUSE_COUNT = 5;

    private final BitSet pressed   = new BitSet(KEY_COUNT);  // currently held
    private final BitSet justDown  = new BitSet(KEY_COUNT);  // went down this frame
    private final BitSet justUp    = new BitSet(KEY_COUNT);  // went up this frame

    // Call at END of each frame to clear single-frame states
    public void endFrame() {
        justDown.clear();
        justUp.clear();
    }

    public boolean isHeld(int keyCode)     { return pressed.get(keyCode); }
    public boolean isJustPressed(int keyCode)  { return justDown.get(keyCode); }
    public boolean isJustReleased(int keyCode) { return justUp.get(keyCode); }

    @Override public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k < KEY_COUNT) {
            if (!pressed.get(k)) justDown.set(k);
            pressed.set(k);
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k < KEY_COUNT) {
            pressed.clear(k);
            justUp.set(k);
        }
    }

    @Override public void keyTyped(KeyEvent e) { /* unused */ }
    // MouseListener methods omitted for brevity; follow same pattern for mouse buttons
}

// ActionMap.java
package com.aetheria.input;

import java.awt.event.KeyEvent;
import java.util.EnumMap;

public final class ActionMap {

    private final InputManager input;
    // Loaded from config; default values are fallbacks
    private final EnumMap<Action, Integer> bindings = new EnumMap<>(Action.class);

    public ActionMap(InputManager input) {
        this.input = input;
        applyDefaults();
    }

    private void applyDefaults() {
        bindings.put(Action.MOVE_UP,      KeyEvent.VK_W);
        bindings.put(Action.MOVE_DOWN,    KeyEvent.VK_S);
        bindings.put(Action.MOVE_LEFT,    KeyEvent.VK_A);
        bindings.put(Action.MOVE_RIGHT,   KeyEvent.VK_D);
        bindings.put(Action.RUN,          KeyEvent.VK_SHIFT);
        bindings.put(Action.INTERACT,     KeyEvent.VK_E);
        bindings.put(Action.ATTACK,       KeyEvent.VK_SPACE);
        bindings.put(Action.OPEN_INVENTORY, KeyEvent.VK_I);
        bindings.put(Action.PAUSE,        KeyEvent.VK_ESCAPE);
        bindings.put(Action.CONFIRM,      KeyEvent.VK_ENTER);
        bindings.put(Action.CANCEL,       KeyEvent.VK_BACK_SPACE);
        bindings.put(Action.DEBUG_TOGGLE, KeyEvent.VK_F3);
    }

    public boolean isHeld(Action a)        { return input.isHeld(bindings.get(a)); }
    public boolean isJustPressed(Action a)  { return input.isJustPressed(bindings.get(a)); }
    public boolean isJustReleased(Action a) { return input.isJustReleased(bindings.get(a)); }

    public void rebind(Action a, int keyCode) {
        bindings.put(a, keyCode);
        // Caller is responsible for persisting to config
    }
}
```

---

## §5 — FRAME TIMER & DEBUG OVERLAY

```java
// FrameTimer.java
package com.aetheria.core.loop;

public final class FrameTimer {
    private static final int SAMPLE_SIZE = 64;
    private final double[] samples = new double[SAMPLE_SIZE];
    private int index = 0;
    private long lastTime = System.nanoTime();

    /** Call once per frame before update(). Returns frame time in ms. */
    public double tick() {
        long now = System.nanoTime();
        double ms = (now - lastTime) / 1_000_000.0;
        lastTime = now;
        samples[index++ % SAMPLE_SIZE] = ms;
        return ms;
    }

    public double getAverageFps() {
        double avgMs = 0;
        for (double s : samples) avgMs += s;
        avgMs /= SAMPLE_SIZE;
        return avgMs > 0 ? 1000.0 / avgMs : 0;
    }

    public double getAverageFrameMs() {
        double sum = 0;
        for (double s : samples) sum += s;
        return sum / SAMPLE_SIZE;
    }
}
```

### DebugOverlay Data Points (F3 screen, always available)
```
FPS: 60.0 (avg: 59.8)     Frame: 14.2ms avg
Entities: 128 / 512        GC Pauses: 3 in last 60s
Player: tile(12,8) px(192,128)    Map: chapter_1/village
Heap: 124MB / 512MB         Systems: mov=0.3ms col=1.2ms rnd=5.1ms
```

---

## §6 — RENDERING BOOTSTRAP (WINDOW + DOUBLE BUFFER)

```java
// Game.java (excerpt — window and rendering setup)
package com.aetheria.core;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class Game extends JPanel {

    private static final int BASE_W = 320;
    private static final int BASE_H = 180;
    private static final int SCALE  = 4;

    // The game renders to this internal buffer at BASE resolution
    private final BufferedImage internalBuffer =
        new BufferedImage(BASE_W, BASE_H, BufferedImage.TYPE_INT_ARGB);

    public void render(double alpha) {
        // 1. Render everything to internalBuffer
        Graphics2D g = internalBuffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.clearRect(0, 0, BASE_W, BASE_H);
        stateManager.render(new Renderer(g, camera), alpha);
        g.dispose();

        // 2. Scale up to window using paintImmediately()
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Nearest-neighbor scaling: CRITICAL for pixel art
        ((Graphics2D) g).setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        g.drawImage(internalBuffer, 0, 0, getWidth(), getHeight(), null);
    }
}
```

**Key rule:** Always render to the `internalBuffer` at `BASE_W × BASE_H`. Scale happens in
`paintComponent` via `drawImage`. This gives true pixel art appearance without blurring.
