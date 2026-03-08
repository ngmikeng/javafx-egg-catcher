package com.eggcatcher.game;

import javafx.scene.input.KeyCode;

/**
 * Central configuration class for the Egg Catcher game.
 * <p>
 * All magic numbers and game constants are defined here as {@code public static final} fields.
 * This makes tuning easy and ensures consistent values across the entire codebase.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public final class GameConfig {

    private GameConfig() {
        // Prevent instantiation — constants-only class
    }

    // ── Screen ──────────────────────────────────────────────────────────────

    /** Game window width in pixels. */
    public static final int SCREEN_WIDTH = 800;

    /** Game window height in pixels. */
    public static final int SCREEN_HEIGHT = 600;

    /** Title displayed in the window title bar. */
    public static final String WINDOW_TITLE = "Egg Catcher — JavaFX";

    // ── Basket ──────────────────────────────────────────────────────────────

    /** Y-coordinate of the basket (from the top). */
    public static final int BASKET_Y = 480;

    /** Basket sprite width in pixels. */
    public static final int BASKET_WIDTH = 80;

    /** Basket sprite height in pixels. */
    public static final int BASKET_HEIGHT = 70;

    /** Basket movement speed in pixels per second (keyboard). */
    public static final double BASKET_SPEED = 400.0;

    // ── Birds ───────────────────────────────────────────────────────────────

    /** Y-coordinate of all birds (top of screen). */
    public static final int BIRD_Y = 30;

    /** Bird sprite width in pixels. */
    public static final int BIRD_WIDTH = 82;

    /** Bird sprite height in pixels. */
    public static final int BIRD_HEIGHT = 88;

    /** Bird horizontal movement speed in pixels per second. */
    public static final double BIRD_SPEED = 80.0;

    // ── Falling Objects ─────────────────────────────────────────────────────

    /** Egg sprite width in pixels. */
    public static final int EGG_WIDTH = 30;

    /** Egg sprite height in pixels. */
    public static final int EGG_HEIGHT = 40;

    /** Shit sprite width in pixels. */
    public static final int SHIT_WIDTH = 30;

    /** Shit sprite height in pixels. */
    public static final int SHIT_HEIGHT = 30;

    /** Initial falling speed of objects in pixels per second. */
    public static final double INITIAL_FALL_SPEED = 80.0;

    /** Additional falling speed added per level-up, in pixels per second. */
    public static final double FALL_SPEED_INCREMENT = 20.0;

    // ── Gameplay ────────────────────────────────────────────────────────────

    /** Number of lives the player starts with. */
    public static final int INITIAL_LIVES = 3;

    /** Score interval that triggers a level-up. Every N eggs caught → level++. */
    public static final int LEVEL_UP_INTERVAL = 20;

    // ── Asset Paths ─────────────────────────────────────────────────────────

    /** Path to the background image resource. */
    public static final String IMG_BACKGROUND = "/images/background.jpg";

    /** Path to the game-over screen image resource. */
    public static final String IMG_GAME_OVER = "/images/gameover.jpg";

    /** Path to the basket sprite resource. */
    public static final String IMG_BASKET = "/images/basket.png";

    /** Path to the egg sprite resource. */
    public static final String IMG_EGG = "/images/egg.png";

    /** Path to the shit sprite resource. */
    public static final String IMG_SHIT = "/images/shit.png";

    // ── Bird Configurations ─────────────────────────────────────────────────

    /**
     * Configuration record for a bird type.
     *
     * @param imagePath    classpath-relative image path
     * @param spawnKey     keyboard key to spawn an additional bird of this type
     * @param layInterval  seconds between egg/shit drops
     * @param shitChance   probability [0..1] that a drop is shit instead of an egg
     */
    public record BirdConfig(String imagePath, KeyCode spawnKey, double layInterval, double shitChance) {}

    /** Chicken: fast layer, low shit chance. */
    public static final BirdConfig CHICKEN = new BirdConfig("/images/chicken.png", KeyCode.G, 5.0, 0.1);

    /** Duck: medium layer, higher shit chance. */
    public static final BirdConfig DUCK = new BirdConfig("/images/duck.png", KeyCode.V, 8.0, 0.25);

    /** Muscovy duck: slow layer, low shit chance. */
    public static final BirdConfig MUSCOVY = new BirdConfig("/images/muscovy.png", KeyCode.N, 15.0, 0.1);

    /** All bird configurations for easy iteration. */
    public static final BirdConfig[] ALL_BIRDS = { CHICKEN, DUCK, MUSCOVY };
}
