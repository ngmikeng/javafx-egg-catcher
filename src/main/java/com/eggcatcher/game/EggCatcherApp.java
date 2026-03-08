package com.eggcatcher.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.eggcatcher.game.scenes.HelpScene;
import com.eggcatcher.game.scenes.MenuScene;
import com.eggcatcher.game.systems.InputManager;

/**
 * Main application entry point for the Egg Catcher game.
 * <p>
 * Owns the primary {@link Stage} and drives navigation between the main menu,
 * help screen, and the game itself. The menu is shown first; from there the
 * player can start a game, read the rules, or exit.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class EggCatcherApp extends Application {

    private Stage primaryStage;
    private GameLoop gameLoop;

    /**
     * JavaFX application entry point. Configures the window and shows the
     * main menu as the initial scene.
     *
     * @param primaryStage the primary window provided by the JavaFX platform
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(GameConfig.WINDOW_TITLE);
        primaryStage.setResizable(false);
        showMenu();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // ── Scene navigation ─────────────────────────────────────────────────────

    private void showMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        primaryStage.setScene(new MenuScene(this::startGame, this::showHelp, Platform::exit).getScene());
    }

    private void showHelp() {
        primaryStage.setScene(new HelpScene(this::showMenu).getScene());
    }

    private void startGame() {
        // ── Canvas ──────────────────────────────────────────────────────────
        Canvas canvas = new Canvas(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Layout ──────────────────────────────────────────────────────────
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Input Manager ───────────────────────────────────────────────────
        InputManager inputManager = new InputManager();
        scene.setOnKeyPressed(e  -> inputManager.onKeyPressed(e.getCode()));
        scene.setOnKeyReleased(e -> inputManager.onKeyReleased(e.getCode()));
        scene.setOnMouseMoved(e  -> inputManager.onMouseMoved(e.getX()));

        // ── Game Loop ───────────────────────────────────────────────────────
        gameLoop = new GameLoop(canvas, inputManager);
        gameLoop.start();

        primaryStage.setScene(scene);
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Called by JavaFX on application shutdown. Stops the game loop.
     */
    @Override
    public void stop() {
        if (gameLoop != null) gameLoop.stop();
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
