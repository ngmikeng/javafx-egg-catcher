package com.eggcatcher.game.entities;

import java.util.Set;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import com.eggcatcher.game.GameConfig;

/**
 * The player-controlled basket at the bottom of the screen.
 * <p>
 * Moves horizontally via keyboard (arrow keys) or mouse/pointer tracking.
 * Position is clamped to screen bounds so the basket never leaves the
 * visible area.
 * </p>
 *
 * <h3>Fixes applied:</h3>
 * <ul>
 *   <li><b>C4:</b> Uses {@code GameConfig.SCREEN_WIDTH} directly — no null {@code game} reference</li>
 * </ul>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class Basket {

    /** Sprite image. */
    private final Image image;

    /** Current x-coordinate (left edge). */
    private double x;

    /** Whether the mouse/pointer is currently controlling position. */
    private boolean mouseControlActive = false;

    /** Last known mouse x-position (used for pointer control). */
    private double mouseX = -1;

    /** Creates a new basket centered horizontally at the bottom. */
    public Basket() {
        this.image = new Image(getClass().getResourceAsStream(GameConfig.IMG_BASKET));
        // Center the basket horizontally
        this.x = (GameConfig.SCREEN_WIDTH - GameConfig.BASKET_WIDTH) / 2.0;
    }

    /**
     * Updates basket position based on keyboard and/or mouse input.
     * <p>
     * Keyboard (arrow keys) moves the basket at a constant speed.
     * Mouse movement instantly sets the basket's x-position.
     * The result is always clamped to {@code [0, SCREEN_WIDTH - BASKET_WIDTH]}.
     * </p>
     *
     * @param delta       seconds since last frame
     * @param pressedKeys set of currently pressed keys
     */
    public void update(double delta, Set<KeyCode> pressedKeys) {
        // Keyboard control
        if (pressedKeys.contains(KeyCode.LEFT)) {
            x -= GameConfig.BASKET_SPEED * delta;
            mouseControlActive = false;
        }
        if (pressedKeys.contains(KeyCode.RIGHT)) {
            x += GameConfig.BASKET_SPEED * delta;
            mouseControlActive = false;
        }

        // Mouse control (if active and no keyboard override)
        if (mouseControlActive && mouseX >= 0) {
            x = mouseX - GameConfig.BASKET_WIDTH / 2.0;
        }

        // Clamp to screen bounds
        if (x < 0) x = 0;
        if (x > GameConfig.SCREEN_WIDTH - GameConfig.BASKET_WIDTH) {
            x = GameConfig.SCREEN_WIDTH - GameConfig.BASKET_WIDTH;
        }
    }

    /**
     * Called when the mouse/pointer moves. Updates the target x-position
     * and activates mouse control mode.
     *
     * @param pointerX the pointer's x-coordinate on the canvas
     */
    public void onMouseMoved(double pointerX) {
        this.mouseX = pointerX;
        this.mouseControlActive = true;
    }

    /**
     * Renders the basket sprite at its current position.
     *
     * @param gc the graphics context to draw on
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, GameConfig.BASKET_Y, GameConfig.BASKET_WIDTH, GameConfig.BASKET_HEIGHT);
    }

    /**
     * Returns the axis-aligned bounding rectangle for collision detection.
     *
     * @return bounding box as a {@link Rectangle2D}
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, GameConfig.BASKET_Y, GameConfig.BASKET_WIDTH, GameConfig.BASKET_HEIGHT);
    }

    /**
     * Resets the basket to its default centered position.
     */
    public void reset() {
        this.x = (GameConfig.SCREEN_WIDTH - GameConfig.BASKET_WIDTH) / 2.0;
        this.mouseControlActive = false;
        this.mouseX = -1;
    }
}
