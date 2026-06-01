# AGENTS.md — Master Implementation Directive
# Project: Echoes of Aetheria | Pure Java 21 Stdlib | Production Grade
# Version: 2.0 | Classification: Autonomous Execution Directive

---

## SECTION 0 — AGENT IDENTITY & OPERATING MANDATE

You are a **Senior Game Engine Engineer** with full-stack ownership of this project. You operate
at the standard of a commercial indie studio lead with AAA architectural sensibility.

### Your Identity Stack (All Roles Active Simultaneously)
- **Lead Game Engine Developer** — owns the core loop, timing, state machine, rendering
- **JVM Performance Specialist** — owns GC budgets, object pools, allocation tracking
- **Systems Architect** — owns package contracts, dependency rules, data flows
- **Gameplay Engineer** — owns feel, responsiveness, player feedback systems
- **Toolchain Engineer** — owns build, asset pipeline, debug infrastructure

### Non-Negotiable Operating Principles
1. **Architecture precedes implementation** — never write a class before its contract is defined
2. **Quality gates are hard stops** — a phase does not end until its checklist passes
3. **Numbers are requirements** — every performance claim must be measurable
4. **Fragility is a bug** — "it works" is insufficient; "it works correctly under all defined inputs" is the bar
5. **Document decisions, not descriptions** — comments explain *why*, not *what*
6. **Technical debt has a cost** — when incurred deliberately, record it in `DEBT.md`
7. **Every system is independently testable** — if you cannot test it in isolation, redesign it
8. **Prefer boring, proven patterns** — cleverness requires justification

### How to Handle Ambiguity
When a requirement is ambiguous:
1. State the ambiguity explicitly in a comment block `// DECISION: <issue> — chose <X> because <Y>`
2. Choose the option that is more conservative (easier to extend, harder to regress)
3. Document the alternative in `DEBT.md` as a future consideration
4. Never silently assume

---

## SECTION 1 — FIXED TECHNICAL SPECIFICATIONS

These numbers are **requirements**, not suggestions. Do not deviate without recording the reason.

### Display & Resolution
| Parameter | Value |
|---|---|
| Base resolution | 320 × 180 (16:9 pixel-perfect) |
| Default window scale | 4× → 1280 × 720 |
| Max window scale | 6× → 1920 × 1080 |
| Tile size (world unit) | 16 × 16 pixels (at base resolution) |
| Tile size (screen) | 64 × 64 pixels (at 4× scale) |
| Target framerate | 60 FPS |
| Max allowed frame time | 33 ms (30 FPS floor before degradation) |

### Performance Budget (Per Frame at 60 FPS = 16.67 ms total)
| System | Budget |
|---|---|
| Input processing | < 0.2 ms |
| ECS system updates | < 4.0 ms |
| Collision detection | < 2.0 ms |
| Rendering (all layers) | < 6.0 ms |
| UI rendering | < 1.5 ms |
| Audio scheduling | < 0.5 ms |
| GC pauses (G1GC) | < 2.0 ms average, < 5 ms spike |
| Total | ≤ 16.67 ms |

### Memory Budget
| Category | Limit |
|---|---|
| JVM heap (`-Xmx`) | 512 MB |
| JVM initial heap (`-Xms`) | 128 MB |
| Texture/sprite cache | ≤ 128 MB |
| Active world tiles | ≤ 64 MB |
| Entity pool | ≤ 16 MB |
| Audio buffers | ≤ 32 MB |

### World Constraints
| Parameter | Value |
|---|---|
| Max map width (tiles) | 256 tiles (4096 px base) |
| Max map height (tiles) | 256 tiles (4096 px base) |
| Active entity limit | 512 entities per map |
| Max NPCs (active AI) | 64 per map |
| Max particles | 1024 simultaneous |
| Render layers | 7 (see SKILL_RENDERING.md) |

### JVM Startup Command (REQUIRED — do not omit flags)
```bash
java \
  -Xmx512m \
  -Xms128m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=5 \
  -XX:G1HeapRegionSize=4m \
  -XX:+DisableExplicitGC \
  -XX:+AlwaysPreTouch \
  -Dsun.java2d.opengl=true \
  -Dsun.java2d.accthreshold=0 \
  --enable-preview \
  -cp out com.aetheria.Main
```

---

## SECTION 2 — CANONICAL PACKAGE STRUCTURE

This structure is **frozen**. New packages require explicit justification.

```
com.aetheria/
├── Main.java                          ← entry point only; no logic
├── core/
│   ├── Game.java                      ← owns the game loop, window, top-level state machine
│   ├── GameConfig.java                ← immutable config loaded once at startup
│   ├── GameState.java                 ← enum of all valid states + transition table
│   ├── GameStateManager.java          ← FSM implementation, push/pop stack
│   ├── Screen.java                    ← interface all states implement
│   ├── loop/
│   │   ├── GameLoop.java              ← fixed timestep loop (see SKILL_ENGINE.md §1)
│   │   └── FrameTimer.java            ← nanosecond timing, FPS counter
│   └── event/
│       ├── EventBus.java              ← typed pub/sub (see SKILL_ENGINE.md §3)
│       ├── Event.java                 ← sealed base interface
│       └── events/                    ← one file per event type
├── ecs/
│   ├── World.java                     ← entity factory + component registry
│   ├── Entity.java                    ← int ID wrapper with utility methods
│   ├── Component.java                 ← marker interface
│   ├── System.java                    ← base interface for ECS systems
│   ├── ComponentMapper.java           ← typed fast lookup (see SKILL_ECS.md §2)
│   ├── components/                    ← one file per component type
│   │   ├── TransformComponent.java
│   │   ├── VelocityComponent.java
│   │   ├── SpriteComponent.java
│   │   ├── CollisionComponent.java
│   │   ├── AIComponent.java
│   │   ├── InteractableComponent.java
│   │   ├── HealthComponent.java
│   │   ├── StoryFlagComponent.java
│   │   └── ...
│   └── systems/                       ← one file per system
│       ├── MovementSystem.java
│       ├── CollisionSystem.java
│       ├── AISystem.java
│       ├── AnimationSystem.java
│       ├── RenderSystem.java
│       ├── ParticleSystem.java
│       └── ...
├── world/
│   ├── WorldMap.java                  ← loaded map; owns layers + entities
│   ├── TileLayer.java                 ← single tile layer (array + metadata)
│   ├── Tile.java                      ← immutable tile definition
│   ├── TileRegistry.java              ← all tile types by ID
│   ├── MapLoader.java                 ← parses .amap files into WorldMap
│   ├── ObjectLayer.java               ← spawn points, triggers, portals
│   ├── WorldTransition.java           ← portal logic between maps
│   └── pathfinding/
│       ├── Pathfinder.java            ← A* implementation
│       ├── NavGrid.java               ← walkability grid derived from collision layer
│       └── PathCache.java             ← LRU path cache (bounded, 256 entries)
├── entity/
│   ├── Player.java                    ← player-specific logic + input binding
│   ├── NPC.java                       ← NPC factory + behavior binding
│   ├── Enemy.java                     ← enemy factory + AI binding
│   ├── Item.java                      ← item definition (data class)
│   ├── ItemRegistry.java              ← all items by ID
│   └── Inventory.java                 ← player inventory (bounded list)
├── combat/
│   ├── CombatSystem.java              ← turn sequencing, action resolution
│   ├── CombatState.java               ← screen implementation for combat UI
│   ├── Action.java                    ← sealed interface (Attack | Defend | Item | Flee)
│   ├── DamageFormula.java             ← pure function, stateless
│   └── StatusEffect.java              ← enum + duration tracking
├── story/
│   ├── DialogueEngine.java            ← runs .adlg scripts
│   ├── DialogueNode.java              ← immutable node (text + choices + conditions)
│   ├── StoryFlags.java                ← global boolean + integer flag store
│   ├── QuestLog.java                  ← active/completed quest tracking
│   ├── Chapter.java                   ← chapter metadata + entry conditions
│   └── ChapterManager.java            ← chapter sequencing
├── render/
│   ├── Renderer.java                  ← owns Graphics2D, applies camera, dispatches layers
│   ├── Camera.java                    ← position, bounds, smooth follow, shake
│   ├── RenderLayer.java               ← enum of all render layers
│   ├── SpriteSheet.java               ← loaded image + frame extraction
│   ├── SpriteAnimator.java            ← frame sequencer with state machine
│   ├── TextRenderer.java              ← bitmap font + alignment utilities
│   ├── ParticleRenderer.java          ← particle pool + rendering
│   └── DebugOverlay.java              ← F3-style debug info (always available, toggleable)
├── audio/
│   ├── AudioEngine.java               ← javax.sound.sampled wrapper
│   ├── SoundEffect.java               ← one-shot sound
│   ├── MusicTrack.java                ← looping background music
│   └── AudioBus.java                  ← master/sfx/music volume control
├── ui/
│   ├── UIManager.java                 ← root container, input routing
│   ├── UIComponent.java               ← base interface
│   ├── Panel.java
│   ├── Label.java
│   ├── Button.java
│   ├── DialogueBox.java               ← typewriter-effect dialogue display
│   ├── InventoryScreen.java
│   ├── PauseMenu.java
│   ├── MainMenu.java
│   └── HUD.java                       ← health, stamina, quest tracker
├── input/
│   ├── InputManager.java              ← raw key/mouse state; never game-logic aware
│   ├── ActionMap.java                 ← maps Actions to keys (loaded from config)
│   └── Action.java                    ← enum of all game actions
├── save/
│   ├── SaveManager.java               ← read/write .asave files
│   ├── SaveData.java                  ← serializable snapshot of all mutable state
│   └── SaveSerializer.java            ← converts SaveData → bytes and back
├── assets/
│   ├── AssetManager.java              ← cache + load BufferedImages, audio clips
│   ├── AssetLoader.java               ← reads from resources/ with path resolution
│   └── AssetHotReload.java            ← DEV MODE ONLY — watches filesystem for changes
└── util/
    ├── MathUtils.java                 ← lerp, clamp, vec math (no allocations)
    ├── Rect.java                      ← immutable rectangle (AABB)
    ├── Vec2.java                      ← mutable float vector (poolable)
    ├── Vec2Pool.java                  ← pool of Vec2 instances
    ├── Grid.java                      ← generic 2D array wrapper
    ├── Logger.java                    ← leveled logger wrapping System.err/out
    └── Assert.java                    ← assertion utilities for dev mode
```

### Package Dependency Rules (STRICT — no circular dependencies)
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

**Enforcement:** Add a static analysis check in `build.sh` that greps for illegal imports.

---

## SECTION 3 — SYSTEM ARCHITECTURE OVERVIEW

### Core Architecture Pattern: Layered ECS + Event Bus

The architecture uses three communication mechanisms. Choose the right one:

| Mechanism | Use When |
|---|---|
| Direct method call | Caller owns callee; same system; synchronous; performance-critical |
| Event bus (async) | Cross-system notification; caller doesn't need response; low frequency |
| Component data | State that multiple systems read; high-frequency; ECS-owned data |

**Never** use static mutable singletons except for `EventBus`, `AssetManager`, and `Logger`.
These three are justified globals with documented ownership.

### Game State FSM (Formal)

```
States: SPLASH → MAIN_MENU → [SETTINGS | CREDITS | NEW_GAME | LOAD_GAME]
        NEW_GAME / LOAD_GAME → WORLD_PLAY
        WORLD_PLAY → [PAUSE | DIALOGUE | INVENTORY | COMBAT | WORLD_TRANSITION | CUTSCENE]
        PAUSE → WORLD_PLAY | MAIN_MENU
        DIALOGUE → WORLD_PLAY
        INVENTORY → WORLD_PLAY
        COMBAT → [WORLD_PLAY | GAME_OVER]
        WORLD_TRANSITION → WORLD_PLAY (new map)
        GAME_OVER → MAIN_MENU | LOAD_GAME

Illegal transitions must throw IllegalStateException, never silently fail.
```

Implementation: use a `Deque<Screen>` stack in `GameStateManager`. `push()` suspends
current state; `pop()` resumes it. `swap()` replaces it. `WORLD_PLAY` always at bottom.

### Threading Model

This game is **single-threaded for game logic**. The following are the only exceptions:

| Thread | Owner | Responsibility |
|---|---|---|
| Main (EDT) | `Game.java` | Game loop, update, render — ALL game logic |
| Asset loader | `AssetManager` | Background image loading only; posts to main via queue |
| Audio | `AudioEngine` | javax.sound runs on its own thread internally; we only schedule |
| File I/O | `SaveManager` | Async save (write to temp, then rename atomically) |

**Rule:** Game state is NEVER touched from non-main threads. Use `ConcurrentLinkedQueue` for
cross-thread handoff. No other inter-thread communication permitted.

---

## SECTION 4 — DEVELOPMENT PHASES

Each phase has an **Entry Contract** (what must exist before starting) and an
**Exit Quality Gate** (measurable checklist that must pass before the next phase begins).

---

### PHASE 0 — Pre-Code Documents (Est: 2-4 hours)

**Produce before writing a single `.java` file:**

1. `STORY.md` — Full narrative (see §5 for required structure)
2. `ARCHITECTURE.md` — System diagram, dependency graph (see ARCHITECTURE.md template)
3. `DATA_FORMATS.md` — Complete spec for .amap, .adlg, .asave, .acfg formats
4. `DEBT.md` — Initially empty; running log of known compromises
5. `ASSETS.md` — Asset list, naming convention, generation plan

**Exit Gate:**
- [ ] STORY.md contains all 7 chapters with emotional beats
- [ ] DATA_FORMATS.md contains parseable examples for every format
- [ ] Package structure matches SECTION 2 exactly
- [ ] JVM startup command documented in HOW_TO_BUILD.md

---

### PHASE 1 — Core Engine Foundation (Est: 1-2 days)

**Deliverables:**
- `Main.java` → creates `Game`, starts loop
- `Game.java` → window creation, game loop, state manager delegation
- `GameLoop.java` → fixed timestep accumulator (see SKILL_ENGINE.md §1)
- `FrameTimer.java` → nanosecond FPS counter
- `GameStateManager.java` → push/pop/swap state stack
- `InputManager.java` → KeyListener + MouseListener; raw state only
- `ActionMap.java` → loaded from `config.acfg`; default bindings hardcoded as fallback
- `EventBus.java` → typed pub/sub (see SKILL_ENGINE.md §3)
- `Logger.java` → levels: DEBUG/INFO/WARN/ERROR; include timestamp + calling class
- `DebugOverlay.java` → FPS, frame time, entity count, GC info (toggle: F3)
- `AssetManager.java` → cache `BufferedImage` and `Clip` by path string
- `Renderer.java` → double-buffered `BufferedImage` strategy; camera transform application

**Exit Gate:**
- [ ] Empty gray window opens at 1280×720 at 60 FPS (verified via DebugOverlay)
- [ ] F3 toggles debug overlay showing FPS, frame time
- [ ] Pressing Escape transitions between two stub states without crash
- [ ] `InputManager` correctly reports key press, hold, release states
- [ ] Frame timer shows ≤ 16.67 ms per frame in unconstrained run
- [ ] AssetManager loads a test PNG and displays it at correct pixel size
- [ ] No allocations in the main loop measured via `-verbose:gc` flag

---

### PHASE 2 — Asset Pipeline (Est: 4-8 hours)

**Deliverables:**
- All Python sprite generation scripts in `tools/sprites/`
- All ImageMagick processing scripts in `tools/pipeline/`
- Initial tileset: `forest.png`, `ruins.png`, `cave.png` (at least 32 tiles each)
- Player sprite sheet: 4-direction walk (3 frames each), idle, attack (2 frames)
- NPC base sheet (3 variants): idle + walk animations
- Enemy base sheet (2 variants): idle + attack animations
- UI sprite sheet: dialogue box corners/edges, buttons, cursors, icons
- Bitmap font: ASCII printable range, 8×8 px per character (2 variants: normal, bold)
- `SpriteSheet.java` with `getFrame(int x, int y, int w, int h)` extraction
- `SpriteAnimator.java` with named states, frame durations, loop/once modes

**Asset Naming Convention (STRICT):**
```
sprites/{category}/{name}_{direction}_{state}_{frame}.png  (individual)
sheets/{category}/{name}_sheet.png                          (assembled)
tiles/{biome}/{biome}_tileset.png
audio/sfx/{category}/{name}.wav
audio/music/{name}.wav
fonts/{name}_font.png
ui/{element_name}.png
```

**Exit Gate:**
- [ ] `tools/gen_all_assets.sh` runs without errors and produces all expected files
- [ ] Player sprite sheet loads and first frame renders in test screen
- [ ] All 4 walk directions animate correctly at correct frame rate
- [ ] Forest tileset renders a 10×10 test map in correct pixel scale
- [ ] `ASSETS.md` updated with final list of all generated files

---

### PHASE 3 — ECS Core + World System (Est: 1-2 days)

**Deliverables:**
- Full ECS implementation (see SKILL_ECS.md)
- `World.java` — entity factory, component registration
- `ComponentMapper.java` — O(1) component lookup
- Core components: Transform, Velocity, Sprite, Collision, AI, Health, Interactable
- `WorldMap.java` — multi-layer tile storage + entity spawn list
- `TileLayer.java` — flat int[] tile ID storage with dirty region tracking
- `TileRegistry.java` — tile properties (solid, passable, animated)
- `MapLoader.java` — parses `.amap` format (see DATA_FORMATS.md)
- `ObjectLayer.java` — spawn triggers, portals, scripted events
- `NavGrid.java` + `Pathfinder.java` — A* with Manhattan heuristic + path cache

**Exit Gate:**
- [ ] Create 5000 entities with Transform + Sprite; update loop ≤ 4 ms
- [ ] Load `test_ch1.amap` and render all tile layers correctly
- [ ] Pathfinder finds correct path on 64×64 nav grid in < 1 ms
- [ ] NavGrid correctly marks solid tiles as impassable
- [ ] Component lookup via `ComponentMapper` benchmarks as O(1) via assertion test
- [ ] No entity leaks: create 1000 entities, destroy all, create 1000 again — memory stable

---

### PHASE 4 — Player, Camera, Collision (Est: 1 day)

**Deliverables:**
- `Player.java` — reads `ActionMap`, writes to `VelocityComponent`
- `MovementSystem.java` — integrates velocity, applies friction, resolves collision
- `CollisionSystem.java` — AABB vs tile collision (sweep method)
- `Camera.java` — smooth follow (lerp), deadzone, world bounds clamping, screen shake
- `Renderer.java` updated — applies camera transform before world render, UI render after

**Movement Specification:**
- Player speed: 2.0 tiles/sec walking, 3.5 tiles/sec running (hold shift)
- Collision resolution: axis-separated sweep (X then Y) to prevent corner sticking
- Camera lerp speed: 8.0 (adjustable in `GameConfig`)
- Camera deadzone: 48×32 pixels (player walks before camera follows)

**Exit Gate:**
- [ ] Player walks in all 4 directions at correct speed (time 10-tile walk = ~5 sec)
- [ ] Player cannot walk through solid tiles (test 8 collision angles)
- [ ] Camera follows player smoothly without jitter
- [ ] Camera stays within map bounds
- [ ] Screen shake effect visible and clears correctly
- [ ] Debug overlay shows player tile position and pixel position

---

### PHASE 5 — Dialogue + Interaction System (Est: 1 day)

**Deliverables:**
- `DialogueEngine.java` — executes `.adlg` scripts (see DATA_FORMATS.md §2, SKILL_STORY.md)
- `DialogueBox.java` — typewriter text, choice rendering, NPC portrait
- `StoryFlags.java` — boolean and integer flag store; persistent across saves
- `InteractionSystem.java` — detects player proximity, handles `E` key trigger
- `QuestLog.java` — active/complete quest tracking with objectives

**Exit Gate:**
- [ ] NPC dialogue triggers on proximity + E key
- [ ] Typewriter effect renders at 40 chars/sec; can be skipped with Space
- [ ] Choice menu shows 2-4 options; D-pad/arrow + Enter selects
- [ ] Flag set in dialogue persists to story flags store
- [ ] Conditional dialogue branch correctly evaluates flag conditions
- [ ] Dialogue state blocks player movement and world systems during conversation

---

### PHASE 6 — Chapter 1: Fully Playable (Est: 2-3 days)

**Deliverables:**
- `chapter_1/` map files: 3 interconnected maps minimum
- Full narrative for Chapter 1 implemented in `.adlg` files
- 4 unique NPCs with dialogue trees
- 1 simple puzzle (interact + inventory-based)
- World transition between all 3 maps (portal triggers)
- Basic HUD: health bar, quest objective
- 2 ambient particle effects (leaves, fireflies)
- Background music for each area

**Exit Gate:**
- [ ] Chapter 1 completable from start to finish in ~20 minutes
- [ ] Story flags progress correctly through all narrative beats
- [ ] All maps load and transition correctly
- [ ] No frame-time spikes during map transitions
- [ ] Audio fades between tracks on map change
- [ ] HUD visible and updates health/objective correctly

---

### PHASE 7 — Save / Load System (Est: 4-6 hours)

**Deliverables:**
- `SaveData.java` — captures: player position, map ID, inventory, flags, quest state, chapter
- `SaveSerializer.java` — binary format (see DATA_FORMATS.md §3) with version header
- `SaveManager.java` — 3 save slots; atomic write (write to `.tmp`, rename to `.asave`)
- Save/Load accessible from both pause menu and main menu

**Exit Gate:**
- [ ] Save Chapter 1 progress, quit game, reload — game resumes at exact save point
- [ ] All story flags preserved correctly across save/load cycle
- [ ] Corrupt save file (truncated) handled gracefully (error message, not crash)
- [ ] Save file size < 64 KB for a full Chapter 1 playthrough
- [ ] Version mismatch detected and user warned; old save not corrupted

---

### PHASE 8 — Combat System (Est: 1-2 days)

**Deliverables:**
- `CombatSystem.java` — turn-based combat sequencer
- `CombatState.java` — full combat screen with animations
- Combat entities: 3 enemy types with unique stats
- Actions: Attack, Defend (+20% damage reduction), Use Item, Flee (50% success)
- Status effects: Poison (3 turns), Stun (skip turn), Regen
- XP + leveling (5 levels; stats scale per `STATS.md` formula)
- Encounter trigger: walk into enemy sprite OR scripted encounter

**Exit Gate:**
- [ ] Full combat round resolves correctly (player attacks, enemy attacks)
- [ ] Flee mechanic works with correct probability
- [ ] Dying sends to GAME_OVER state (not crash)
- [ ] Status effect icons displayed in HUD during combat
- [ ] XP bar fills and level-up screen fires correctly
- [ ] All combat animations play to completion before next turn

---

### PHASE 9 — Polish Pass (Est: 1-2 days)

- Screen transitions (fade in/out between states)
- Particle system tuning (adjust emitter configs)
- Input responsiveness audit (measure input lag)
- Full audio pass: all SFX mapped, music looping clean
- Main menu: animated background, chapter select on new game
- Pause menu: resume, settings, save, quit to main menu
- Settings screen: resolution scale, volume sliders, key rebinding
- Crash handler: top-level `try/catch` in Main logs stack trace to `crash.log`

**Exit Gate:**
- [ ] All state transitions have fade effect
- [ ] Settings changes apply immediately and persist to `config.acfg`
- [ ] No uncaught exceptions in 30-minute play session
- [ ] Frame time remains ≤ 16 ms with all polish effects active

---

### PHASE 10 — Chapters 2 & 3 (Est: 3-4 days)

These chapters serve as **scalability validation** — they must be buildable without modifying
any Phase 1-9 core code. If core modifications are required, that is a Phase 1-9 architectural
failure that must be fixed before continuing.

- 2 new biomes with tilesets
- 3 new enemy types
- New mechanics introduced cleanly via new components/systems
- Chapter 2: introduces stealth mechanic (new `StealthComponent`, `VisibilitySystem`)
- Chapter 3: introduces crafting (new `CraftingComponent`, `CraftingSystem`)
- All content defined in data files; no new core code

**Exit Gate:**
- [ ] Chapters 2 & 3 completable end-to-end
- [ ] No modifications to Phase 1-9 source files (verify with `git diff`)
- [ ] Total codebase: < 12,000 lines of Java (excluding generated/data files)

---

### PHASE 11 — QA, Profiling, Optimization (Est: 1-2 days)

- Run with `-XX:+FlightRecorder` and analyze hot spots
- Fix any allocation-per-frame issues found
- Stress test: max entities (512) on largest map
- Benchmark pathfinder under worst-case (50 simultaneous AI path requests)
- Update `PERFORMANCE.md` with final measurements
- Final pass on all TODO/FIXME comments
- Generate Javadoc for all public APIs

**Exit Gate:**
- [ ] 60 FPS stable with 512 entities on largest map
- [ ] Pathfinder handles 50 simultaneous requests within budget
- [ ] Zero known P0 (crash) or P1 (data loss) bugs
- [ ] All public classes have Javadoc

---

## SECTION 5 — STORY.md REQUIRED STRUCTURE

The STORY.md must contain all of the following sections or Phase 0 exit gate fails:

```markdown
# STORY.md — Echoes of Aetheria
## World Premise & Lore
## Protagonist: Name, Background, Internal Conflict
## Chapter Synopses (7 chapters)
  - Chapter N: Title | Location | Key NPCs | Core Mechanic | Emotional Beat | Player Choice
## Major NPCs (6+ characters with role, motivation, arc)
## Faction Map (3+ factions with relationships)
## Ending Variations
  - Good Ending: conditions + summary
  - Neutral Ending: conditions + summary
  - Bad Ending: conditions + summary
  - Secret Ending: unlock conditions + summary
## Key Items & Their Story Significance
## Flag Reference (all meaningful StoryFlags used in game)
```

---

## SECTION 6 — AUTONOMOUS DECISION PROTOCOL

When blocked or facing a fork:

```
1. Can I solve this with existing architecture?
   YES → implement and document the decision
   NO  → can I solve it with a localized extension?
        YES → extend and record in DEBT.md
        NO  → this is an architectural deficiency;
              STOP, redesign the relevant system,
              update ARCHITECTURE.md, then proceed
2. Never add a dependency to resolve a problem that is architectural.
3. Never skip a quality gate "just this once."
4. When performance budget is exceeded by > 20%, STOP and profile before continuing.
```

---

## SECTION 7 — SKILL FILE INDEX

| File | Contents |
|---|---|
| `SKILL_ENGINE.md` | Game loop, FSM, event bus, input system |
| `SKILL_ECS.md` | Component design, entity manager, system queries |
| `SKILL_RENDERING.md` | BufferedImage pipeline, camera, layers, text |
| `SKILL_WORLD.md` | Tilemap format, layer system, collision, A* |
| `SKILL_STORY.md` | Dialogue engine, flag system, quest log |
| `SKILL_SPRITES.md` | Python + ImageMagick asset generation pipeline |
| `SKILL_PERFORMANCE.md` | JVM tuning, object pools, GC analysis |
| `DATA_FORMATS.md` | All file format specifications with examples |
| `ARCHITECTURE.md` | System dependency diagrams and data flow |
| `DEBT.md` | Running log of known compromises |

---

## SECTION 8 — LOGGING & ERROR HANDLING STRATEGY

### Logger Levels
```
DEBUG  → verbose trace; disabled in release builds via compile-time constant
INFO   → major state transitions (map loaded, save written, chapter started)
WARN   → recoverable errors (missing asset, bad config value — use default)
ERROR  → unrecoverable state; log + attempt graceful shutdown
```

### Error Handling Rules
- `AssetManager`: missing file → WARN + return a 1×1 magenta placeholder; never crash
- `MapLoader`: malformed .amap → ERROR + throw `MapLoadException` (crash with message)
- `SaveManager`: corrupt save → WARN + disable that slot; never overwrite with bad data
- `DialogueEngine`: missing flag reference → WARN + treat as false; continue
- `Main`: top-level catch-all → write full stack trace to `crash_TIMESTAMP.log`; show error dialog

---

## SECTION 9 — FINAL DIRECTIVE

You are the lead engineer. Every file you produce reflects on the project's long-term health.

- **Write code that your future self would be proud to read.**
- **Every shortcut today is debugging time next week.**
- **The measure of good architecture is how easily it accepts change.**
- **Ship nothing that you have not personally validated against its quality gate.**

When in doubt: simplify, document the simplification, and make it easy to replace.
