package com.eggcatcher.game.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import com.eggcatcher.game.GameConfig;

/**
 * Abstract base class for objects that fall from birds (eggs and shit).
 * <p>
 * Each instance carries its own {@code speed} field — fixing the original
 * codebase's critical bug where a {@code static int dy} was shared across
 * all instances, causing every egg and shit on screen to change speed
 * simultaneously when the level increased.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 * @see Egg
 * @see Shit
 */
public abstract class FallingObject {

    /** Sprite image. */
    protected final Image image;

    /** Current x-coordinate (left edge). */
    protected final double x;

    /** Current y-coordinate (top edge). */
    protected double y;

    /** Falling speed in pixels per second — instance-level, not static. */
    protected final double speed;

    /** Sprite width in pixels. */
    protected final int width;

    /** Sprite height in pixels. */
    protected final int height;

    /** Whether this object has already been "processed" for going off-screen. */
    private boolean offScreenHandled = false;

    /**
     * Creates a new falling object.
     *
     * @param imagePath classpath-relative path to the sprite image
     * @param x         initial x-coordinate (typically the bird's x position)
     * @param y         initial y-coordinate (typically just below the bird)
     * @param speed     falling speed in pixels per second (from current {@code GameState.fallSpeed})
     * @param width     sprite width
     * @param height    sprite height
     */
    protected FallingObject(String imagePath, double x, double y, double speed, int width, int height) {
        this.image = new Image(getClass().getResourceAsStream(imagePath));
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.width = width;
        this.height = height;
    }

    /**
     * Moves the object downward based on elapsed time.
     *
     * @param delta time elapsed since last frame in seconds
     */
    public void update(double delta) {
        y += speed * delta;
    }

    /**
     * Returns {@code true} when the object has fallen below the screen.
     *
     * @return whether the object is no longer visible
     */
    public boolean isOffScreen() {
        return y > GameConfig.SCREEN_HEIGHT;
    }

    /**
     * Returns {@code true} if this off-screen event has already been handled
     * (life already decremented). Prevents the original bug where life was
     * decremented every frame for each invisible egg.
     *
     * @return whether the off-screen penalty was already applied
     */
    public boolean isOffScreenHandled() {
        return offScreenHandled;
    }

    /**
     * Marks this object's off-screen event as handled so the penalty
     * is only applied once.
     */
    public void markOffScreenHandled() {
        this.offScreenHandled = true;
    }

    /**
     * Returns the axis-aligned bounding rectangle for collision detection.
     *
     * @return bounding box as a {@link Rectangle2D}
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    /**
     * Renders the sprite at its current position.
     *
     * @param gc the graphics context to draw on
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, x, y, width, height);
    }
}
