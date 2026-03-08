package com.eggcatcher.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.eggcatcher.game.systems.InputManager;

/**
 * Main application entry point for the Egg Catcher game.
 * <p>
 * Sets up the JavaFX window, canvas, input handlers, and starts the game loop.
 * Replaces the original Swing {@code JFrame} with a proper JavaFX
 * {@link Application} subclass.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class EggCatcherApp extends Application {

    /**
     * JavaFX application entry point. Sets up the stage, scene, canvas,
     * input wiring, and starts the game loop.
     *
     * @param primaryStage the primary window provided by the JavaFX platform
     */
    @Override
    public void start(Stage primaryStage) {
        // ── Canvas ──────────────────────────────────────────────────────────
        Canvas canvas = new Canvas(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Layout ──────────────────────────────────────────────────────────
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Input Manager ───────────────────────────────────────────────────
        InputManager inputManager = new InputManager();

        scene.setOnKeyPressed(event -> inputManager.onKeyPressed(event.getCode()));
        scene.setOnKeyReleased(event -> inputManager.onKeyReleased(event.getCode()));
        scene.setOnMouseMoved(event -> inputManager.onMouseMoved(event.getX()));

        // ── Game Loop ───────────────────────────────────────────────────────
        GameLoop gameLoop = new GameLoop(canvas, inputManager);
        gameLoop.start();

        // ── Stage Configuration ─────────────────────────────────────────────
        primaryStage.setTitle(GameConfig.WINDOW_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> {
            gameLoop.stop();
        });
        primaryStage.show();

        // Ensure the canvas can receive key events
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
    }

    /**
     * Main method — launches the JavaFX application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
