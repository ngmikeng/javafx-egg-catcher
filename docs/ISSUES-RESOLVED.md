# Issues Resolved

This document maps each of the 22 issues identified in the original Java Swing codebase review to how they were addressed in the JavaFX migration.

## 🔴 Critical Issues

### C1 — Game logic executed inside `paint()`

**Original:** `Chicken.paint()`, `Duck.paint()`, `Muscovy.paint()` called `layEgg()`, `layShit()`, and `move()` on child objects, causing unpredictable behavior since `paint()` can be called at any time by the Swing toolkit.

**Fix:** All game logic is now in `Bird.update(double delta, GameState)`, which is called exactly once per frame from `GameLoop.handle()`. The `Bird.render(GraphicsContext)` method performs only drawing — zero game logic.

**Files:** `Bird.java` (update vs. render separation), `GameLoop.java` (orchestrates update → render)

---

### C2 — `ConcurrentModificationException` risk / skipped elements

**Original:** Forward-index loops with `.remove(j)` inside `Board.java` collision methods skipped elements when items were removed.

**Fix:** `CollisionSystem.java` uses **backward iteration** (`for (int i = list.size() - 1; i >= 0; i--)`) for all list removal operations. When an element is removed at index `i`, previously iterated elements are unaffected.

**Files:** `CollisionSystem.java`

---

### C3 — Static `dy` field on `Egg` and `Shit`

**Original:** `private static int dy = 1` in `Egg.java` and `public static int dy = 1` in `Shit.java` shared speed across all instances. Any speed change affected every object on screen simultaneously.

**Fix:** `FallingObject` uses an **instance field** `protected final double speed` set via constructor parameter. Each egg/shit receives the current fall speed from `GameState.getFallSpeed()` at spawn time.

**Files:** `FallingObject.java`, `Bird.java` (passes `gameState.getFallSpeed()` to constructors)

---

### C4 — Null `game` reference accessed for static fields

**Original:** Every entity class declared `private Game game;` but never initialized it. Code like `game.S_WIDTH` only worked because Java resolves static members at compile time.

**Fix:** All entity classes use `GameConfig.SCREEN_WIDTH` (direct class-level access). No entity holds a reference to the application class. The `Game` field is completely eliminated.

**Files:** All entity classes, `GameConfig.java`

---

### C5 — Calling `g.dispose()` inside `paint(Graphics g)`

**Original:** `Board.paint()` called `g.dispose()` on the toolkit-owned `Graphics` object, potentially corrupting subsequent paint operations.

**Fix:** `GameRenderer` never calls `gc.dispose()`. JavaFX manages the `GraphicsContext` lifecycle automatically.

**Files:** `GameRenderer.java`

---

## 🟠 Major Issues

### M1 — Massive code duplication across bird classes

**Original:** `Chicken.java`, `Duck.java`, `Muscovy.java` were ~95% identical (~400 lines of duplication).

**Fix:** Abstract `Bird` base class contains all shared logic (movement, spawning, rendering, list management). Each subclass is a **one-line constructor** that passes its `BirdConfig`:

```java
public class Chicken extends Bird {
    public Chicken() { super(GameConfig.CHICKEN); }
}
```

**Reduction:** ~400 lines → ~30 lines (3 subclasses × ~10 lines each)

**Files:** `Bird.java`, `Chicken.java`, `Duck.java`, `Muscovy.java`, `GameConfig.BirdConfig`

---

### M2 — Collision detection code triplicated

**Original:** `checkChickenCollision()`, `checkDuckCollision()`, `checkMuscovyCollision()` in `Board.java` — ~200 lines of nearly identical code.

**Fix:** Single `CollisionSystem.checkCollisions(basket, birds, gameState)` method iterates all birds generically. No type-specific collision code exists.

**Files:** `CollisionSystem.java`

---

### M3 — Timer interval extremely fast (5 ms)

**Original:** `javax.swing.Timer(5, this)` produced ~200 FPS, consuming excessive CPU and tying movement speed to frame rate.

**Fix:** JavaFX `AnimationTimer` runs at ~60 FPS (synchronized to display refresh). All movement uses **delta-time** (`speed * delta` pixels per frame). A `MAX_DELTA` cap (50 ms) prevents physics explosion after focus loss.

**Files:** `GameLoop.java`, `Bird.java`, `FallingObject.java`, `Basket.java`

---

### M4 — No separation of concerns

**Original:** `Board.java` (377 lines) mixed game state, rendering, input handling, collision detection, entity management, and level logic.

**Fix:** Cleanly separated into:
- `GameState.java` — score, lives, level
- `GameRenderer.java` — canvas rendering
- `InputManager.java` — keyboard + mouse state
- `CollisionSystem.java` — collision detection
- `SpawnSystem.java` — bird spawning
- `GameLoop.java` — orchestration

**Files:** All systems and rendering classes

---

### M5 — Level increments multiply incorrectly

**Original:** `if (score > 0 && score % 20 == 0)` inside per-egg collision loops. Multiple eggs caught at the same score threshold caused multiple level-ups.

**Fix:** `GameState.addScore(int)` uses integer division: `int newLevel = (score / LEVEL_UP_INTERVAL) + 1`. This always produces the correct level regardless of how many points are added or when.

**Files:** `GameState.java`

---

### M6 — `setLevelGame()` method is never called

**Original:** Dead code in `Board.java` — method existed but was never invoked.

**Fix:** Simply not ported. Level logic is cleanly handled inside `GameState.addScore()`.

---

## 🟡 Moderate Issues

### W1 — Raw types used for `ArrayList`

**Original:** Some `ArrayList` declarations lacked generic type parameters.

**Fix:** All collections use proper generics: `List<Bird>`, `List<Egg>`, `List<Shit>`, `Set<KeyCode>`, etc.

**Files:** All classes using collections

---

### W2 — Wasted object creation in `keyPressed()`

**Original:** `Chicken c = new Chicken(); chickenList.add(new Chicken());` — first object created but never used.

**Fix:** `SpawnSystem.update()` creates exactly one object per spawn and adds it to the list. No wasted allocations.

**Files:** `SpawnSystem.java`

---

### W3 — `Random` instantiated on every paint call

**Original:** `new Random()` created ~200 times/second per bird inside `paint()`.

**Fix:** Single `private static final Random RNG = new Random()` in `Bird` class, shared across all instances and frames.

**Files:** `Bird.java`

---

### W4 — Life decremented for every invisible egg every frame

**Original:** `if (egg.isVisible == false) life--;` ran every frame for every off-screen egg, decrementing life repeatedly until the egg was removed.

**Fix:** `CollisionSystem` removes off-screen eggs from the list **immediately** upon detection and calls `gameState.loseLife()` exactly once per egg.

**Files:** `CollisionSystem.java`

---

### W5 — No bounds checking for spawned birds

**Original:** `x = rand.nextInt(game.S_WIDTH)` could place birds partially off-screen (bird width = 82, max x could be 799).

**Fix:** `Bird` constructor clamps: `x = RNG.nextDouble() * (SCREEN_WIDTH - BIRD_WIDTH)`, ensuring the bird is always fully on-screen.

**Files:** `Bird.java`

---

### W6 — Should override `paintComponent()` vs `paint()`

**Original:** `Board` overrode `paint()` instead of `paintComponent()`, bypassing Swing's double-buffering.

**Fix:** Not applicable — JavaFX uses `Canvas` + `GraphicsContext` which provides hardware-accelerated double-buffered rendering automatically. No `paint()` override exists.

**Files:** `GameRenderer.java`

---

## 🟢 Minor Issues

### S1 — Inconsistent naming

**Original:** Vietnamese filenames (`ga.png`, `vit.png`, `ngan.png`, `Ro.png`) mixed with English class names.

**Fix:** All assets renamed to English: `chicken.png`, `duck.png`, `muscovy.png`, `basket.png`. Method names follow Java conventions consistently.

**Files:** `src/main/resources/images/`, `GameConfig.java`

---

### S2 — Unused imports

**Original:** `ImageIcon`, `Point` imported but never used.

**Fix:** Zero unused imports in the new codebase. Only necessary imports are present.

**Files:** All Java files

---

### S3 — No access modifiers on some fields

**Original:** `isVisible` on `Egg` and `Shit` was `public` with no encapsulation.

**Fix:** All fields are `private` or `protected` with proper accessor methods. `FallingObject` exposes `isOffScreen()` as a method rather than a public flag.

**Files:** `FallingObject.java`

---

### S4 — Magic numbers throughout

**Original:** `800`, `600`, `480`, `82`, `88`, `30`, `40`, `100`, `20` used as literal values.

**Fix:** All extracted to named constants in `GameConfig.java`: `SCREEN_WIDTH`, `SCREEN_HEIGHT`, `BASKET_Y`, `BIRD_WIDTH`, `BIRD_HEIGHT`, `EGG_WIDTH`, `EGG_HEIGHT`, `LEVEL_UP_INTERVAL`, `INITIAL_LIVES`, etc.

**Files:** `GameConfig.java`

---

### S5 — No Javadoc or meaningful comments

**Original:** Comments like `// Constructor`, `// Move method` repeated the obvious.

**Fix:** Full Javadoc on all public classes and methods. Inline comments explain non-obvious logic (delta-time, collision iteration, level-up math). Architecture docs provided in `docs/`.

**Files:** All Java files, `docs/ARCHITECTURE.md`

---

## Summary

| Severity | Count | All Resolved? |
| -------- | ----- | ------------- |
| 🔴 Critical | 5 | ✅ Yes |
| 🟠 Major | 6 | ✅ Yes |
| 🟡 Moderate | 6 | ✅ Yes |
| 🟢 Minor | 5 | ✅ Yes |
| **Total** | **22** | **✅ All 22 resolved** |
