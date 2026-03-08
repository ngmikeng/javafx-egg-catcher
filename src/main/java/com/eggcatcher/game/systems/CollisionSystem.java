package com.eggcatcher.game.systems;

import java.util.List;

import javafx.geometry.Rectangle2D;

import com.eggcatcher.game.entities.Basket;
import com.eggcatcher.game.entities.Bird;
import com.eggcatcher.game.entities.Egg;
import com.eggcatcher.game.entities.Shit;
import com.eggcatcher.game.state.GameState;

/**
 * Unified collision detection system.
 * <p>
 * Replaces the three nearly-identical {@code checkChickenCollision()},
 * {@code checkDuckCollision()}, {@code checkMuscovyCollision()} methods
 * from the original codebase (~200 lines of duplicated code → one method).
 * </p>
 *
 * <h3>Fixes applied:</h3>
 * <ul>
 *   <li><b>M2:</b> Single unified collision method for all bird types</li>
 *   <li><b>C2:</b> Backward iteration prevents element skipping on removal</li>
 *   <li><b>W4:</b> Off-screen eggs are removed immediately — life decremented exactly once</li>
 *   <li><b>M5:</b> Level-up logic is inside {@link GameState#addScore}, called once per egg</li>
 * </ul>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class CollisionSystem {

    /**
     * Checks all collisions between the basket and every bird's falling objects.
     * <p>
     * Uses backward iteration ({@code i--}) for safe in-place removal from
     * {@code ArrayList}, fixing the original C2 bug where forward-index
     * removal skipped elements.
     * </p>
     *
     * @param basket    the player's basket
     * @param birds     all active birds (each owns lists of eggs and shits)
     * @param gameState mutable game state for scoring and life changes
     */
    public void checkCollisions(Basket basket, List<Bird> birds, GameState gameState) {
        if (gameState.isGameOver()) return;

        Rectangle2D basketBounds = basket.getBounds();

        for (Bird bird : birds) {
            checkEggCollisions(basketBounds, bird.getEggs(), gameState);
            checkShitCollisions(basketBounds, bird.getShits(), gameState);
        }
    }

    /**
     * Checks egg collisions and off-screen eggs for a single bird.
     * <p>
     * <b>Backward iteration</b> ensures removal doesn't skip elements.
     * Off-screen eggs are removed immediately and penalize the player
     * exactly once (fixes W4).
     * </p>
     */
    private void checkEggCollisions(Rectangle2D basketBounds, List<Egg> eggs, GameState gameState) {
        for (int i = eggs.size() - 1; i >= 0; i--) {
            Egg egg = eggs.get(i);

            // Caught by basket → +1 score, remove
            if (intersects(basketBounds, egg.getBounds())) {
                eggs.remove(i);
                gameState.addScore(1);
                continue;
            }

            // Fell off screen → -1 life, remove (exactly once)
            if (egg.isOffScreen()) {
                eggs.remove(i);
                gameState.loseLife();
            }
        }
    }

    /**
     * Checks shit collisions and off-screen shits for a single bird.
     * <p>
     * Caught shit → lose life. Off-screen shit → harmlessly removed (no penalty).
     * </p>
     */
    private void checkShitCollisions(Rectangle2D basketBounds, List<Shit> shits, GameState gameState) {
        for (int i = shits.size() - 1; i >= 0; i--) {
            Shit shit = shits.get(i);

            // Caught by basket → -1 life, remove
            if (intersects(basketBounds, shit.getBounds())) {
                shits.remove(i);
                gameState.loseLife();
                continue;
            }

            // Fell off screen → just remove (no penalty for avoiding shit)
            if (shit.isOffScreen()) {
                shits.remove(i);
            }
        }
    }

    /**
     * Tests whether two rectangles overlap (axis-aligned bounding box test).
     *
     * @param a first rectangle
     * @param b second rectangle
     * @return {@code true} if the rectangles intersect
     */
    private boolean intersects(Rectangle2D a, Rectangle2D b) {
        return a.intersects(b);
    }
}
