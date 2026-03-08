package com.eggcatcher.game.rendering;

import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.eggcatcher.game.GameConfig;
import com.eggcatcher.game.entities.Basket;
import com.eggcatcher.game.entities.Bird;
import com.eggcatcher.game.state.GameState;

/**
 * Renders the entire game scene onto a JavaFX {@link Canvas}.
 * <p>
 * This class performs <em>only</em> drawing — no game logic, no state
 * mutation, no input handling. This fixes the original C1 bug where
 * game logic was interleaved with rendering inside {@code paint()}.
 * </p>
 * <p>
 * Does <em>not</em> call {@code gc.dispose()} — JavaFX manages the
 * {@link GraphicsContext} lifecycle (fixes original C5 bug).
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class GameRenderer {

    /** The canvas to draw on. */
    private final Canvas canvas;

    /** Graphics context obtained once from the canvas. */
    private final GraphicsContext gc;

    /** Background image for normal gameplay. */
    private final Image background;

    /** Overlay image shown when the game is over. */
    private final Image gameOverImage;

    /** Font for HUD text. */
    private final Font hudFont;

    /** Font for game-over overlay text. */
    private final Font gameOverFont;

    /**
     * Creates a new renderer for the given canvas.
     *
     * @param canvas the canvas to render onto
     */
    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.background = new Image(getClass().getResourceAsStream(GameConfig.IMG_BACKGROUND));
        this.gameOverImage = new Image(getClass().getResourceAsStream(GameConfig.IMG_GAME_OVER));
        this.hudFont = Font.font("Arial", FontWeight.BOLD, 16);
        this.gameOverFont = Font.font("Arial", FontWeight.BOLD, 24);
    }

    /**
     * Renders a complete frame of the game.
     * <p>
     * When the game is active, draws the background, all entities, and the HUD.
     * When the game is over, draws the game-over overlay with final score and
     * restart instructions.
     * </p>
     *
     * @param basket    the player's basket
     * @param birds     all active birds (each renders its own eggs/shits)
     * @param gameState current game state (for HUD and game-over detection)
     */
    public void render(Basket basket, List<Bird> birds, GameState gameState) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // Clear the canvas
        gc.clearRect(0, 0, w, h);

        if (!gameState.isGameOver()) {
            renderGameplay(basket, birds, gameState, w, h);
        } else {
            renderGameOver(gameState, w, h);
        }
    }

    /**
     * Draws the active gameplay scene: background, entities, HUD.
     */
    private void renderGameplay(Basket basket, List<Bird> birds, GameState gameState, double w, double h) {
        // Background
        gc.drawImage(background, 0, 0, w, h);

        // Entities
        for (Bird bird : birds) {
            bird.render(gc);
        }
        basket.render(gc);

        // HUD — top-left
        gc.setFont(hudFont);
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);

        String scoreText = "Score: " + gameState.getScore();
        String lifeText = "Lives: " + gameState.getLife();
        String levelText = "Level: " + gameState.getLevel();

        // Draw with outline for readability over any background
        gc.strokeText(scoreText, 10, 25);
        gc.fillText(scoreText, 10, 25);

        gc.strokeText(lifeText, 10, 45);
        gc.fillText(lifeText, 10, 45);

        gc.strokeText(levelText, w - 80, 25);
        gc.fillText(levelText, w - 80, 25);
    }

    /**
     * Draws the game-over screen with final score and restart prompt.
     */
    private void renderGameOver(GameState gameState, double w, double h) {
        // Game-over background
        gc.drawImage(gameOverImage, 0, 0, w, h);

        // Semi-transparent overlay for text readability
        gc.setFill(Color.color(0, 0, 0, 0.5));
        gc.fillRect(0, h / 2 - 60, w, 120);

        // Final score text
        gc.setFont(gameOverFont);
        gc.setFill(Color.WHITE);

        String finalScore = "Final Score: " + gameState.getScore();
        String levelReached = "Level Reached: " + gameState.getLevel();
        String restartHint = "Press ENTER to restart";

        gc.fillText(finalScore, w / 2 - 100, h / 2 - 20);
        gc.fillText(levelReached, w / 2 - 100, h / 2 + 10);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gc.fillText(restartHint, w / 2 - 85, h / 2 + 45);
    }
}
