package com.eggcatcher.game.entities;

import com.eggcatcher.game.GameConfig;

/**
 * A chicken — the fastest egg layer with a low shit chance (10%).
 * <p>
 * Spawned at game start and additionally via the <b>G</b> key.
 * Lay interval: {@value com.eggcatcher.game.GameConfig#CHICKEN}.layInterval seconds.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class Chicken extends Bird {

    /** Creates a new chicken with the standard configuration. */
    public Chicken() {
        super(GameConfig.CHICKEN);
    }
}
