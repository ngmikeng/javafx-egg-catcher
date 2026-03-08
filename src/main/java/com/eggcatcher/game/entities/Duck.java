package com.eggcatcher.game.entities;

import com.eggcatcher.game.GameConfig;

/**
 * A duck — medium-speed egg layer with a higher shit chance (25%).
 * <p>
 * Spawned at game start and additionally via the <b>V</b> key.
 * Lay interval: {@value com.eggcatcher.game.GameConfig#DUCK}.layInterval seconds.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class Duck extends Bird {

    /** Creates a new duck with the standard configuration. */
    public Duck() {
        super(GameConfig.DUCK);
    }
}
