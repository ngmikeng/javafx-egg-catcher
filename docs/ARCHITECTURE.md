# Architecture Overview

## System Architecture

The Egg Catcher game follows a clean **Entity-System-Renderer** pattern with a centralized game loop driven by JavaFX's `AnimationTimer`.

```
EggCatcherApp (Application)
 │
 ├── Scene
 │    ├── Canvas (800×600)
 │    └── Input event handlers ──► InputManager
 │
 └── GameLoop (AnimationTimer, ~60 FPS)
      │
      ├── Systems (update phase)
      │    ├── SpawnSystem     ← creates birds on key press
      │    ├── InputManager    ← tracks keyboard/mouse state
      │    └── CollisionSystem ← detects basket↔egg/shit intersections
      │
      ├── Entities (updated per frame)
      │    ├── Basket          ← responds to keyboard + mouse
      │    └── List<Bird>      ← each bird owns its Egg[] and Shit[]
      │         ├── Chicken
      │         ├── Duck
      │         └── Muscovy
      │
      ├── State
      │    └── GameState       ← score, lives, level, game-over flag
      │
      └── GameRenderer (render phase)
           ├── Background
           ├── Birds + falling objects
           ├── Basket
           ├── HUD (score, lives, level)
           └── Game-over overlay
```

## Game Loop Flow

Each frame (~16.6 ms at 60 FPS), the `AnimationTimer.handle(long now)` method executes:

```
1. Compute delta time (seconds since last frame)
2. Cap delta to MAX_DELTA (0.05s) to prevent physics explosion
3. If game over:
   a. Check ENTER key → restart
   b. Skip to render
4. SpawnSystem.update()     → add birds if G/V/N just pressed
5. Basket.update()          → move based on keyboard + mouse
6. Bird.update() (for each) → horizontal movement, lay timer, egg/shit update
7. CollisionSystem.check()  → catch eggs (+score), catch shit (-life), miss eggs (-life)
8. InputManager.consumeJustPressed() → clear one-shot keys
9. GameRenderer.render()    → draw everything
```

## Class Hierarchy

### Entities

```
Bird (abstract)
 ├── Chicken      ← config: chicken.png, key=G, interval=5s, 10% shit
 ├── Duck         ← config: duck.png, key=V, interval=8s, 25% shit
 └── Muscovy      ← config: muscovy.png, key=N, interval=15s, 10% shit

FallingObject (abstract)
 ├── Egg          ← caught = +1 score, missed = -1 life
 └── Shit         ← caught = -1 life, missed = harmless

Basket            ← player-controlled, keyboard + mouse
```

### Systems

```
InputManager      ← tracks pressed/just-pressed keys + mouse position
SpawnSystem       ← creates Bird instances on spawn-key press
CollisionSystem   ← AABB intersection tests, backward iteration for safe removal
```

### State

```
GameState         ← score, lives, level, game-over flag, computed fall speed
GameConfig        ← all constants, BirdConfig records
```

### Rendering

```
GameRenderer      ← Canvas/GraphicsContext drawing, HUD, game-over overlay
```

## Key Design Decisions

### Canvas + GraphicsContext (vs. Scene Graph)

Chosen because:
- Closest to the original Swing `Graphics2D` approach
- More efficient for many rapidly-moving sprites
- Full control over draw order
- Simpler to port from the original codebase

### AnimationTimer (vs. Timeline / ScheduledExecutor)

- Provides nanosecond timestamps for precise delta-time calculation
- Automatically synchronized with the display refresh rate (~60 FPS)
- Standard pattern for JavaFX game development

### Abstract Bird base class (vs. interface / composition)

- Chosen to share state (fields) and behavior (methods) — an interface with default methods can't hold state
- Subclasses are trivial (1 line constructor) — they only provide configuration
- `BirdConfig` record holds type-specific data (image, key, interval, shit chance)

### Single Random instance (vs. new Random() per call)

- Original code created `new Random()` ~200 times per second per bird
- Now a single `static final Random RNG` in `Bird` is shared across all instances

### Backward iteration for collision removal

- `for (int i = list.size() - 1; i >= 0; i--)` ensures no elements are skipped when removing
- Simpler and more readable than `Iterator.remove()` for index-based ArrayList access

## Data Flow

```
User Input ──► InputManager ──► SpawnSystem ──► List<Bird>
                    │                               │
                    │                               ▼
                    └──► Basket ◄── CollisionSystem ──► GameState
                                                         │
                                                         ▼
                                                   GameRenderer ──► Canvas
```

## Threading Model

All game logic runs on the **JavaFX Application Thread**:
- `AnimationTimer.handle()` is called on the FX thread
- Input events are delivered on the FX thread
- Canvas rendering is on the FX thread

No concurrency issues, no synchronization needed.
