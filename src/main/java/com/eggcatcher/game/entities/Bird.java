package com.eggcatcher.game.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import com.eggcatcher.game.GameConfig;
import com.eggcatcher.game.GameConfig.BirdConfig;
import com.eggcatcher.game.state.GameState;

/**
 * Abstract base class for all bird types (Chicken, Duck, Muscovy).
 * <p>
 * Eliminates ~400 lines of duplicated code from the original codebase by
 * extracting common movement, spawning, and rendering logic into a single
 * class. Concrete subclasses need only call {@code super(...)} with their
 * specific {@link BirdConfig}.
 * </p>
 *
 * <h3>Fixes applied (from code review):</h3>
 * <ul>
 *   <li><b>C1:</b> Game logic (egg/shit spawning) moved out of render — now in {@link #update}</li>
 *   <li><b>M1:</b> All bird code consolidated into this base class</li>
 *   <li><b>W3:</b> Single {@link Random} instance reused (not created every frame)</li>
 *   <li><b>W5:</b> Spawn position clamped to screen bounds</li>
 *   <li><b>C3:</b> Each egg/shit gets instance-level speed from {@link GameState#getFallSpeed()}</li>
 *   <li><b>C4:</b> Uses {@code GameConfig.SCREEN_WIDTH} directly, no null game reference</li>
 * </ul>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 * @see Chicken
 * @see Duck
 * @see Muscovy
 */
public abstract class Bird {

    /** Shared random number generator — reused across all frames. */
    private static final Random RNG = new Random();

    /** Configuration for this bird type. */
    protected final BirdConfig config;

    /** Sprite image loaded once at construction time. */
    protected final Image image;

    /** Current x-coordinate (left edge). */
    protected double x;

    /** Horizontal direction: +1 = right, -1 = left. */
    protected int dx;

    /** Eggs currently falling from this bird. */
    private final List<Egg> eggs = new ArrayList<>();

    /** Shit droppings currently falling from this bird. */
    private final List<Shit> shits = new ArrayList<>();

    /** Countdown timer (in seconds) until the next egg/shit is laid. */
    private double layTimer;

    /**
     * Creates a new bird of the given type.
     *
     * @param config bird-type configuration (image, spawn key, intervals, etc.)
     */
    protected Bird(BirdConfig config) {
        this.config = config;
        this.image = new Image(getClass().getResourceAsStream(config.imagePath()));
        // Random start position, clamped so the bird is fully on-screen (fixes W5)
        this.x = RNG.nextDouble() * (GameConfig.SCREEN_WIDTH - GameConfig.BIRD_WIDTH);
        this.dx = RNG.nextBoolean() ? 1 : -1;
        this.layTimer = config.layInterval();
    }

    // ── Update ──────────────────────────────────────────────────────────────

    /**
     * Updates the bird's state for one frame.
     * <p>
     * Handles horizontal movement (bouncing off walls), egg/shit spawn timer,
     * and updates all owned falling objects. All game logic is here — not in
     * {@link #render}, fixing the original C1 bug.
     * </p>
     *
     * @param delta     seconds elapsed since last frame
     * @param gameState current game state (provides fall speed for new drops)
     */
    public void update(double delta, GameState gameState) {
        // ── Horizontal movement (bounce off walls) ──
        double move = GameConfig.BIRD_SPEED * delta * dx;
        x += move;
        if (x <= 0) {
            x = 0;
            dx = 1;
        } else if (x >= GameConfig.SCREEN_WIDTH - GameConfig.BIRD_WIDTH) {
            x = GameConfig.SCREEN_WIDTH - GameConfig.BIRD_WIDTH;
            dx = -1;
        }

        // ── Lay timer ──
        layTimer -= delta;
        if (layTimer <= 0) {
            lay(gameState);
            layTimer = config.layInterval();
        }

        // ── Update falling objects ──
        for (Egg egg : eggs) {
            egg.update(delta);
        }
        for (Shit shit : shits) {
            shit.update(delta);
        }
    }

    /**
     * Spawns an egg or shit based on the configured probability.
     *
     * @param gameState provides the current fall speed
     */
    private void lay(GameState gameState) {
        double spawnX = x + (GameConfig.BIRD_WIDTH - GameConfig.EGG_WIDTH) / 2.0;
        double spawnY = GameConfig.BIRD_Y + GameConfig.BIRD_HEIGHT;
        double fallSpeed = gameState.getFallSpeed();

        if (RNG.nextDouble() < config.shitChance()) {
            shits.add(new Shit(spawnX, spawnY, fallSpeed));
        } else {
            eggs.add(new Egg(spawnX, spawnY, fallSpeed));
        }
    }

    // ── Rendering ───────────────────────────────────────────────────────────

    /**
     * Renders the bird and all its owned falling objects.
     * <p>
     * This method performs <em>only</em> drawing — no game logic (fixes C1).
     * </p>
     *
     * @param gc the graphics context to draw on
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, GameConfig.BIRD_Y, GameConfig.BIRD_WIDTH, GameConfig.BIRD_HEIGHT);

        for (Egg egg : eggs) {
            egg.render(gc);
        }
        for (Shit shit : shits) {
            shit.render(gc);
        }
    }

    // ── Accessors ───────────────────────────────────────────────────────────

    /**
     * Returns the bounding rectangle for this bird.
     *
     * @return axis-aligned bounding box
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, GameConfig.BIRD_Y, GameConfig.BIRD_WIDTH, GameConfig.BIRD_HEIGHT);
    }

    /**
     * Returns the mutable list of eggs currently falling from this bird.
     * The collision system removes caught/off-screen eggs from this list.
     *
     * @return list of active eggs
     */
    public List<Egg> getEggs() {
        return eggs;
    }

    /**
     * Returns the mutable list of shit droppings falling from this bird.
     * The collision system removes caught/off-screen shits from this list.
     *
     * @return list of active shit droppings
     */
    public List<Shit> getShits() {
        return shits;
    }

    /**
     * Returns the bird-type configuration.
     *
     * @return this bird's {@link BirdConfig}
     */
    public BirdConfig getConfig() {
        return config;
    }
}
