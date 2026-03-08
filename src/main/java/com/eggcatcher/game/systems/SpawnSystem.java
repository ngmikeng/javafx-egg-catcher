package com.eggcatcher.game.systems;

import java.util.List;

import com.eggcatcher.game.GameConfig;
import com.eggcatcher.game.GameConfig.BirdConfig;
import com.eggcatcher.game.entities.Bird;
import com.eggcatcher.game.entities.Chicken;
import com.eggcatcher.game.entities.Duck;
import com.eggcatcher.game.entities.Muscovy;

/**
 * Handles spawning of new bird instances at runtime.
 * <p>
 * Checks the {@link InputManager} for spawn-key presses (G → Chicken,
 * V → Duck, N → Muscovy) and creates new bird instances, adding them
 * to the active bird list. Fixes the original W2 bug where a new object
 * was created but discarded unused.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class SpawnSystem {

    /**
     * Checks for spawn-key presses and adds new birds to the list.
     *
     * @param inputManager the input manager to query for key presses
     * @param birds        the mutable list of active birds to add to
     */
    public void update(InputManager inputManager, List<Bird> birds) {
        for (BirdConfig config : GameConfig.ALL_BIRDS) {
            if (inputManager.isJustPressed(config.spawnKey())) {
                birds.add(createBird(config));
            }
        }
    }

    /**
     * Factory method to create a bird instance from its configuration.
     *
     * @param config the bird type configuration
     * @return a new bird instance of the appropriate subclass
     */
    private Bird createBird(BirdConfig config) {
        if (config == GameConfig.CHICKEN) return new Chicken();
        if (config == GameConfig.DUCK) return new Duck();
        if (config == GameConfig.MUSCOVY) return new Muscovy();
        throw new IllegalArgumentException("Unknown bird config: " + config);
    }
}
