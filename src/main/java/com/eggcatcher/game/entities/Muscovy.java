package com.eggcatcher.game.entities;

import com.eggcatcher.game.GameConfig;

/**
 * A Muscovy duck — the slowest egg layer with a low shit chance (10%).
 * <p>
 * Spawned at game start and additionally via the <b>N</b> key.
 * Lay interval: {@value com.eggcatcher.game.GameConfig#MUSCOVY}.layInterval seconds.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class Muscovy extends Bird {

    /** Creates a new Muscovy duck with the standard configuration. */
    public Muscovy() {
        super(GameConfig.MUSCOVY);
    }
}
