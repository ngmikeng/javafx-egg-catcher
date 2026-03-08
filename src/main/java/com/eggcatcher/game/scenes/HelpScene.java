package com.eggcatcher.game.scenes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.eggcatcher.game.GameConfig;

/**
 * "How to Play" help scene reachable from the main menu.
 * <p>
 * Explains controls, scoring rules, and bird-spawning shortcuts.
 * The back button navigates back to the menu via the {@code onBack} callback.
 * </p>
 */
public class HelpScene {

    private final Scene scene;

    /**
     * Builds the help scene.
     *
     * @param onBack called when the user clicks "Back to Menu"
     */
    public HelpScene(Runnable onBack) {
        // ── Background canvas ────────────────────────────────────────────────
        Canvas canvas = new Canvas(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image bg = new Image(getClass().getResourceAsStream(GameConfig.IMG_BACKGROUND));
        gc.drawImage(bg, 0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        gc.setFill(Color.color(0, 0, 0, 0.70));
        gc.fillRect(0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Content ──────────────────────────────────────────────────────────
        Font body = Font.font("Arial", 16);
        Font bold = Font.font("Arial", FontWeight.BOLD, 16);

        VBox content = new VBox(9);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(620);

        content.getChildren().add(heading("How to Play"));
        content.getChildren().add(gap(8));
        content.getChildren().add(line("Move basket:   Arrow Keys  or  Mouse", body));
        content.getChildren().add(line("Catch falling eggs to earn points.", body));
        content.getChildren().add(line("Avoid bird droppings — each one costs a life.", body));
        content.getChildren().add(line("Miss 3 eggs off-screen and it's game over.", body));
        content.getChildren().add(line("Every 20 points advances you to the next level.", body));
        content.getChildren().add(line("Speed increases with each level — good luck!", body));
        content.getChildren().add(gap(12));
        content.getChildren().add(label("Spawn Extra Birds:", bold, Color.GOLD));
        content.getChildren().add(line("G  —  Spawn a Chicken   (slow layer, low droppings)", body));
        content.getChildren().add(line("V  —  Spawn a Duck      (medium speed, more droppings)", body));
        content.getChildren().add(line("N  —  Spawn a Muscovy   (fast, lots of droppings)", body));
        content.getChildren().add(gap(20));
        content.getChildren().add(MenuScene.menuButton("Back to Menu", onBack));

        // ── Layout ───────────────────────────────────────────────────────────
        StackPane root = new StackPane(canvas, content);
        StackPane.setAlignment(content, Pos.CENTER);
        scene = new Scene(root, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }

    /** Returns the built JavaFX {@link Scene}. */
    public Scene getScene() {
        return scene;
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private static Label heading(String text) {
        return label(text, Font.font("Arial", FontWeight.BOLD, 36), Color.GOLD);
    }

    private static Label line(String text, Font font) {
        return label(text, font, Color.WHITE);
    }

    private static Label label(String text, Font font, Color color) {
        Label l = new Label(text);
        l.setFont(font);
        l.setTextFill(color);
        return l;
    }

    private static Region gap(double height) {
        Region r = new Region();
        r.setPrefHeight(height);
        return r;
    }
}
