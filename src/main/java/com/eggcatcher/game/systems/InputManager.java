package com.eggcatcher.game.systems;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;

/**
 * Centralized input manager that tracks keyboard and mouse state.
 * <p>
 * Registered on the JavaFX {@code Scene} via event handlers. Provides
 * a clean API for the game loop to query current input state without
 * coupling entities directly to JavaFX event objects.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class InputManager {

    /** Keys currently held down. */
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    /**
     * Keys that were pressed this frame (for one-shot actions like bird spawning).
     * Cleared after each frame via {@link #consumeJustPressed()}.
     */
    private final Set<KeyCode> justPressedKeys = new HashSet<>();

    /** Current mouse x-position on the canvas. -1 if never moved. */
    private double mouseX = -1;

    /** Whether the mouse has been moved at least once. */
    private boolean mouseMoved = false;

    // ── Event Handlers (called from Scene) ──────────────────────────────────

    /**
     * Called when a key is pressed. Adds the key to both held and just-pressed sets.
     *
     * @param code the key code
     */
    public void onKeyPressed(KeyCode code) {
        if (!pressedKeys.contains(code)) {
            justPressedKeys.add(code);
        }
        pressedKeys.add(code);
    }

    /**
     * Called when a key is released. Removes the key from the held set.
     *
     * @param code the key code
     */
    public void onKeyReleased(KeyCode code) {
        pressedKeys.remove(code);
    }

    /**
     * Called when the mouse moves over the canvas.
     *
     * @param x the mouse x-coordinate
     */
    public void onMouseMoved(double x) {
        this.mouseX = x;
        this.mouseMoved = true;
    }

    // ── Query API ───────────────────────────────────────────────────────────

    /**
     * Returns the set of keys currently held down.
     *
     * @return unmodifiable view of pressed keys
     */
    public Set<KeyCode> getPressedKeys() {
        return Set.copyOf(pressedKeys);
    }

    /**
     * Returns {@code true} if the key was pressed this frame (one-shot).
     * This is consumed by {@link #consumeJustPressed()} at the end of
     * each frame to prevent repeated triggers.
     *
     * @param code the key to check
     * @return whether the key was just pressed
     */
    public boolean isJustPressed(KeyCode code) {
        return justPressedKeys.contains(code);
    }

    /**
     * Returns the current mouse x-position.
     *
     * @return x-coordinate, or -1 if the mouse hasn't moved yet
     */
    public double getMouseX() {
        return mouseX;
    }

    /**
     * Returns whether the mouse has been moved at least once.
     *
     * @return true if mouse position data is available
     */
    public boolean isMouseMoved() {
        return mouseMoved;
    }

    /**
     * Clears the just-pressed key set. Called once per frame after
     * all systems have had a chance to read it.
     */
    public void consumeJustPressed() {
        justPressedKeys.clear();
    }

    /**
     * Resets all input state (for game restart).
     */
    public void reset() {
        pressedKeys.clear();
        justPressedKeys.clear();
        mouseX = -1;
        mouseMoved = false;
    }
}
