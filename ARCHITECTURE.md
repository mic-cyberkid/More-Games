# ARCHITECTURE.md — Echoes of Aetheria

## System Architecture Overview
The engine follows a **Layered Entity Component System (ECS)** architecture, supported by a central **Event Bus** for decoupled communication and a **Fixed Timestep Game Loop** for deterministic logic.

### Core Patterns
1. **ECS (Entity Component System):**
   - **Entities:** Simple integer IDs.
   - **Components:** Data-only structures (POJOs).
   - **Systems:** Logic that operates on entities possessing specific component sets.
2. **Event Bus (Typed Pub/Sub):**
   - Used for cross-system notifications (e.g., `PlayerHealthChangedEvent` → `HUD`).
   - Prevents tight coupling between unrelated systems.
3. **State Stack (Push/Pop FSM):**
   - Manages game states (Menu, World, Dialogue, Combat).
   - Allows overlays (Dialogue over World) naturally.

## Package Dependencies & Rules
The project enforces a strict dependency hierarchy to prevent circular references:

```
util        ← no dependencies (leaf)
assets      ← util
input       ← util
core        ← util, assets, input
ecs         ← util, core
render      ← util, assets, ecs
world       ← util, ecs, render
entity      ← util, ecs, world
combat      ← util, ecs, entity
story       ← util, ecs, entity
audio       ← util, assets
ui          ← util, render, input, story
save        ← util, ecs, world, entity, story
Main        ← core (ONLY — everything else via core.Game)
```

## Threading Model
Single-threaded for all game logic to ensure determinism and avoid concurrency overhead.

| Thread | Responsibility |
|---|---|
| **Main (EDT)** | Game loop, ECS updates, Rendering. |
| **Asset Loader** | Background loading of heavy resources (images, sounds). |
| **Audio** | Internal JVM audio mixing/playback. |
| **File I/O** | Atomic save/load operations. |

## Data Flow
1. **Input:** `InputManager` captures raw events → `ActionMap` translates to `Action` enums.
2. **Update:** `GameLoop` calls `GameStateManager.update(dt)` → Active `Screen` updates systems.
3. **ECS:** Systems query the `World` for entities → Modify `Component` data.
4. **Events:** Systems post to `EventBus` → Subscribers react (e.g., UI updates).
5. **Render:** `GameLoop` calls `GameStateManager.render(r, alpha)` → `Renderer` draws layers based on `Component` state.
