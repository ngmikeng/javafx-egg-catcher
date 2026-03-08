package com.eggcatcher.game.entities;

import com.eggcatcher.game.GameConfig;

/**
 * A falling egg — the player's goal is to catch these with the basket.
 * <p>
 * Catching an egg awards +1 score. Missing it (falling off-screen)
 * costs the player one life.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class Egg extends FallingObject {

    /**
     * Creates a new egg at the given position with the specified fall speed.
     *
     * @param x     x-coordinate (left edge)
     * @param y     y-coordinate (top edge, typically just below the bird)
     * @param speed falling speed in pixels per second
     */
    public Egg(double x, double y, double speed) {
        super(GameConfig.IMG_EGG, x, y, speed, GameConfig.EGG_WIDTH, GameConfig.EGG_HEIGHT);
    }
}
