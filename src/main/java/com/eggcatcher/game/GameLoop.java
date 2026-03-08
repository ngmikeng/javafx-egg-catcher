package com.eggcatcher.game;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

import com.eggcatcher.game.entities.Basket;
import com.eggcatcher.game.entities.Bird;
import com.eggcatcher.game.entities.Chicken;
import com.eggcatcher.game.entities.Duck;
import com.eggcatcher.game.entities.Muscovy;
import com.eggcatcher.game.rendering.GameRenderer;
import com.eggcatcher.game.state.GameState;
import com.eggcatcher.game.systems.CollisionSystem;
import com.eggcatcher.game.systems.InputManager;
import com.eggcatcher.game.systems.SpawnSystem;

/**
 * Main game loop using JavaFX {@link AnimationTimer}.
 * <p>
 * Replaces the original {@code javax.swing.Timer(5ms)} with a proper
 * JavaFX animation timer that runs at ~60 FPS and uses delta-time for
 * frame-rate-independent movement. This fixes the original M3 bug
 * (excessive CPU from 200 Hz timer) and ensures consistent gameplay
 * speed regardless of hardware.
 * </p>
 *
 * <h3>Update order (fixes M4 — clear separation of concerns):</h3>
 * <ol>
 *   <li>Process spawn requests (InputManager → SpawnSystem)</li>
 *   <li>Update basket position (keyboard + mouse)</li>
 *   <li>Update all birds (movement, lay timer, falling objects)</li>
 *   <li>Check collisions (eggs caught, shits caught, eggs off-screen)</li>
 *   <li>Consume just-pressed keys</li>
 *   <li>Render everything</li>
 * </ol>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class GameLoop extends AnimationTimer {

    // ── Dependencies ────────────────────────────────────────────────────────
    private final GameState gameState;
    private final InputManager inputManager;
    private final SpawnSystem spawnSystem;
    private final CollisionSystem collisionSystem;
    private final GameRenderer renderer;

    // ── Entities ────────────────────────────────────────────────────────────
    private Basket basket;
    private List<Bird> birds;

    // ── Timing ──────────────────────────────────────────────────────────────
    /** Previous frame timestamp in nanoseconds. -1 means first frame. */
    private long previousTime = -1;

    /** Maximum delta to prevent physics from exploding after a pause. */
    private static final double MAX_DELTA = 0.05; // 50 ms = 20 FPS minimum

    /**
     * Creates the game loop with all required systems and begins a new game.
     *
     * @param canvas       the canvas on which the game is rendered
     * @param inputManager the shared input manager (already wired to the Scene)
     */
    public GameLoop(Canvas canvas, InputManager inputManager) {
        this.gameState = new GameState();
        this.inputManager = inputManager;
        this.spawnSystem = new SpawnSystem();
        this.collisionSystem = new CollisionSystem();
        this.renderer = new GameRenderer(canvas);

        initEntities();
    }

    /**
     * Called by JavaFX ~60 times per second. Computes delta time and
     * delegates to the update/render pipeline.
     *
     * @param now current timestamp in nanoseconds
     */
    @Override
    public void handle(long now) {
        if (previousTime < 0) {
            previousTime = now;
            return; // skip first frame (no delta yet)
        }

        double delta = (now - previousTime) / 1_000_000_000.0; // nanoseconds → seconds
        previousTime = now;

        // Cap delta to avoid physics explosion after focus loss / debugger pause
        if (delta > MAX_DELTA) {
            delta = MAX_DELTA;
        }

        update(delta);
        render();
    }

    /**
     * Updates all game systems for one frame.
     *
     * @param delta seconds elapsed since last frame
     */
    private void update(double delta) {
        if (gameState.isGameOver()) {
            // Check for restart key
            if (inputManager.isJustPressed(javafx.scene.input.KeyCode.ENTER)) {
                restart();
            }
            inputManager.consumeJustPressed();
            return;
        }

        // 1. Spawn new birds on key press
        spawnSystem.update(inputManager, birds);

        // 2. Update basket
        basket.update(delta, inputManager.getPressedKeys());
        if (inputManager.isMouseMoved()) {
            basket.onMouseMoved(inputManager.getMouseX());
        }

        // 3. Update all birds (movement, laying, falling objects)
        for (Bird bird : birds) {
            bird.update(delta, gameState);
        }

        // 4. Check collisions
        collisionSystem.checkCollisions(basket, birds, gameState);

        // 5. Consume one-shot key presses
        inputManager.consumeJustPressed();
    }

    /**
     * Renders the current frame.
     */
    private void render() {
        renderer.render(basket, birds, gameState);
    }

    /**
     * Initializes all game entities for a new game.
     * Creates one bird of each type plus the player's basket.
     */
    private void initEntities() {
        basket = new Basket();
        birds = new ArrayList<>();
        birds.add(new Chicken());
        birds.add(new Duck());
        birds.add(new Muscovy());
    }

    /**
     * Pauses the game loop. The last rendered frame remains on the canvas.
     * Call {@link #resume()} to continue.
     */
    public void pause() {
        stop(); // AnimationTimer.stop()
    }

    /**
     * Resumes a paused game loop.
     * Resets the frame timer so there is no physics jump from the pause gap.
     */
    public void resume() {
        previousTime = -1; // prevent huge delta after the pause gap
        start(); // AnimationTimer.start()
    }

    /**
     * Restarts the game: resets state, clears entities, creates fresh ones.
     */
    private void restart() {
        gameState.reset();
        inputManager.reset();
        initEntities();
        previousTime = -1; // reset timer to avoid huge delta on restart
    }
}
