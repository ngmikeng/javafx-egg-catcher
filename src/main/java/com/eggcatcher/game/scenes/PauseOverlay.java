package com.eggcatcher.game.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.eggcatcher.game.GameConfig;

/**
 * Semi-transparent pause overlay added on top of the frozen game canvas.
 * <p>
 * Added to / removed from the game scene's root {@link StackPane} whenever
 * the player presses ESC. Because the overlay lives inside the existing game
 * scene rather than replacing it, the frozen game frame stays visible behind
 * the panel.
 * </p>
 * <p>
 * Buttons: Resume, Restart, How to Play, Exit.
 * </p>
 */
public class PauseOverlay extends StackPane {

    /**
     * Creates the pause overlay.
     *
     * @param onResume  called when "Resume" is clicked
     * @param onRestart called when "Restart" is clicked
     * @param onHelp    called when "How to Play" is clicked
     * @param onExit    called when "Exit" is clicked
     */
    public PauseOverlay(Runnable onResume, Runnable onRestart, Runnable onHelp, Runnable onExit) {
        // Fill the full canvas area
        setPrefSize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        setBackground(new Background(new BackgroundFill(
                Color.color(0, 0, 0, 0.62), CornerRadii.EMPTY, Insets.EMPTY)));
        setAlignment(Pos.CENTER);

        // ── Title ────────────────────────────────────────────────────────────
        Label title = new Label("Paused");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setTextFill(Color.GOLD);

        // ── Buttons ──────────────────────────────────────────────────────────
        Button resumeBtn  = MenuScene.menuButton("Resume",      onResume);
        Button restartBtn = MenuScene.menuButton("Restart",     onRestart);
        Button helpBtn    = MenuScene.menuButton("How to Play", onHelp);
        Button exitBtn    = MenuScene.menuButton("Exit",        onExit);

        // ── Layout ───────────────────────────────────────────────────────────
        VBox layout = new VBox(14, title, resumeBtn, restartBtn, helpBtn, exitBtn);
        layout.setAlignment(Pos.CENTER);
        VBox.setMargin(resumeBtn, new Insets(16, 0, 0, 0));

        getChildren().add(layout);
    }
}
