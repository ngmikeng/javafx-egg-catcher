package com.eggcatcher.game.state;

import com.eggcatcher.game.GameConfig;

/**
 * Centralized mutable state for the Egg Catcher game.
 * <p>
 * Holds score, lives, level, and game-over status. All mutation goes through
 * dedicated methods ({@link #addScore(int)}, {@link #loseLife()}) that enforce
 * consistent level-up and game-over logic in a single place — eliminating the
 * original codebase's duplicated level checks spread across multiple collision methods.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class GameState {

    private int score;
    private int life;
    private int level;
    private boolean gameOver;

    /** Creates a new game state with default initial values. */
    public GameState() {
        reset();
    }

    // ── Accessors ───────────────────────────────────────────────────────────

    /** Returns the current score. */
    public int getScore() {
        return score;
    }

    /** Returns the number of remaining lives. */
    public int getLife() {
        return life;
    }

    /** Returns the current level (1-based). */
    public int getLevel() {
        return level;
    }

    /** Returns {@code true} when the game is over (lives ≤ 0). */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Computes the current falling speed for newly spawned objects.
     * <p>
     * Speed increases with each level:
     * {@code INITIAL_FALL_SPEED + (level - 1) * FALL_SPEED_INCREMENT}
     * </p>
     *
     * @return falling speed in pixels per second
     */
    public double getFallSpeed() {
        return GameConfig.INITIAL_FALL_SPEED
                + (level - 1) * GameConfig.FALL_SPEED_INCREMENT;
    }

    // ── Mutators ────────────────────────────────────────────────────────────

    /**
     * Adds points to the score and checks for level-up.
     * <p>
     * Level increases whenever the score crosses a multiple of
     * {@link GameConfig#LEVEL_UP_INTERVAL}. The check uses integer division
     * so the level increments exactly once per threshold, regardless of how
     * many eggs are caught in a single frame.
     * </p>
     *
     * @param points number of points to add (typically 1)
     */
    public void addScore(int points) {
        score += points;
        int newLevel = (score / GameConfig.LEVEL_UP_INTERVAL) + 1;
        if (newLevel > level) {
            level = newLevel;
        }
    }

    /**
     * Decrements the player's life by one.
     * Sets the game-over flag when lives reach zero.
     */
    public void loseLife() {
        if (gameOver) return;          // prevent further decrements after death
        life--;
        if (life <= 0) {
            life = 0;
            gameOver = true;
        }
    }

    /**
     * Resets all state to initial values, allowing the game to restart.
     */
    public void reset() {
        score = 0;
        life = GameConfig.INITIAL_LIVES;
        level = 1;
        gameOver = false;
    }

    @Override
    public String toString() {
        return String.format("GameState[score=%d, life=%d, level=%d, gameOver=%b]",
                score, life, level, gameOver);
    }
}
