package com.eggcatcher.game.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.eggcatcher.game.GameConfig;

/**
 * Main menu scene shown when the application first launches.
 * <p>
 * Displays the game title over the background image and three navigation
 * buttons: Start Game, How to Play, and Exit. Callbacks are passed in at
 * construction time so this class stays decoupled from the Stage.
 * </p>
 */
public class MenuScene {

    // ── Button styles ────────────────────────────────────────────────────────

    static final String BTN_STYLE =
            "-fx-background-color: rgba(0,0,0,0.45);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 17px;" +
            "-fx-font-family: 'Arial';" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: rgba(255,255,255,0.75);" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;";

    static final String BTN_HOVER =
            "-fx-background-color: rgba(255,200,0,0.45);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 17px;" +
            "-fx-font-family: 'Arial';" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: gold;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;";

    // ── Fields ───────────────────────────────────────────────────────────────

    private final Scene scene;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Builds the menu scene.
     *
     * @param onStart called when the user clicks "Start Game"
     * @param onHelp  called when the user clicks "How to Play"
     * @param onExit  called when the user clicks "Exit"
     */
    public MenuScene(Runnable onStart, Runnable onHelp, Runnable onExit) {
        // ── Background canvas ────────────────────────────────────────────────
        Canvas canvas = new Canvas(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image bg = new Image(getClass().getResourceAsStream(GameConfig.IMG_BACKGROUND));
        gc.drawImage(bg, 0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        gc.setFill(Color.color(0, 0, 0, 0.55));
        gc.fillRect(0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Title ────────────────────────────────────────────────────────────
        Label titleLabel = new Label("Egg Catcher");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 54));
        titleLabel.setTextFill(Color.GOLD);

        Label subtitle = new Label("Catch the eggs - dodge the droppings!");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.color(1, 1, 1, 0.8));

        // ── Buttons ──────────────────────────────────────────────────────────
        Button startBtn = menuButton("Start Game",  onStart);
        Button helpBtn  = menuButton("How to Play", onHelp);
        Button exitBtn  = menuButton("Exit",        onExit);

        // ── Layout ───────────────────────────────────────────────────────────
        VBox layout = new VBox(14, titleLabel, subtitle, startBtn, helpBtn, exitBtn);
        layout.setAlignment(Pos.CENTER);
        VBox.setMargin(startBtn, new Insets(18, 0, 0, 0));

        StackPane root = new StackPane(canvas, layout);
        scene = new Scene(root, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /** Returns the built JavaFX {@link Scene}. */
    public Scene getScene() {
        return scene;
    }

    // ── Package-private helpers ──────────────────────────────────────────────

    /**
     * Creates a styled menu button shared by this scene and {@link HelpScene}.
     *
     * @param text   button label
     * @param action callback to run on click
     * @return the configured button
     */
    static Button menuButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefWidth(230);
        btn.setPrefHeight(46);
        btn.setStyle(BTN_STYLE);
        btn.setOnMouseEntered(e -> btn.setStyle(BTN_HOVER));
        btn.setOnMouseExited(e  -> btn.setStyle(BTN_STYLE));
        btn.setOnAction(e -> action.run());
        return btn;
    }
}
