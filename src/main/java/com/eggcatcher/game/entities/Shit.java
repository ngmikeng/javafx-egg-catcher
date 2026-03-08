package com.eggcatcher.game.entities;

import com.eggcatcher.game.GameConfig;

/**
 * A falling dropping (shit) — the player must avoid catching these.
 * <p>
 * If the basket catches a shit, the player loses one life.
 * Shit that falls off-screen is harmlessly removed (no penalty).
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class Shit extends FallingObject {

    /**
     * Creates a new shit dropping at the given position with the specified fall speed.
     *
     * @param x     x-coordinate (left edge)
     * @param y     y-coordinate (top edge, typically just below the bird)
     * @param speed falling speed in pixels per second
     */
    public Shit(double x, double y, double speed) {
        super(GameConfig.IMG_SHIT, x, y, speed, GameConfig.SHIT_WIDTH, GameConfig.SHIT_HEIGHT);
    }
}
