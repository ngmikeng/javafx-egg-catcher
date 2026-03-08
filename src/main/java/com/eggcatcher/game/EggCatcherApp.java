package com.eggcatcher.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.eggcatcher.game.scenes.HelpScene;
import com.eggcatcher.game.scenes.MenuScene;
import com.eggcatcher.game.scenes.PauseOverlay;
import com.eggcatcher.game.systems.InputManager;

/**
 * Main application entry point for the Egg Catcher game.
 * <p>
 * Owns the primary {@link Stage} and drives navigation between the main menu,
 * help screen, pause overlay, and the game itself.
 * </p>
 *
 * <h3>Pause flow</h3>
 * <ol>
 *   <li>Player presses ESC during gameplay → {@link #showPause()} stops the loop
 *       and adds a {@link PauseOverlay} on top of the frozen game canvas.</li>
 *   <li>Resume → overlay removed, loop restarted.</li>
 *   <li>Restart → a fresh game is started.</li>
 *   <li>How to Play → switches to {@link HelpScene}; back returns to the paused
 *       game scene (with overlay still in place).</li>
 *   <li>Exit → {@link Platform#exit()}.</li>
 * </ol>
 * <p>
 * Pressing ESC again while paused also resumes, giving a keyboard shortcut.
 * </p>
 *
 * @author Egg Catcher Team
 * @version 1.0.0
 */
public class EggCatcherApp extends Application {

    private Stage primaryStage;
    private GameLoop gameLoop;

    /** The game scene — kept alive while paused so the frozen canvas stays visible. */
    private Scene gameScene;

    /** Root StackPane of the game scene; the pause overlay is added here. */
    private StackPane gameRoot;

    /** Whether the game is currently paused. */
    private boolean paused = false;

    /** The pause overlay currently displayed, or {@code null} when not paused. */
    private PauseOverlay pauseOverlay;

    // ── JavaFX lifecycle ──────────────────────────────────────────────────────

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(GameConfig.WINDOW_TITLE);
        primaryStage.setResizable(false);
        showMenu();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (gameLoop != null) gameLoop.stop();
    }

    // ── Scene navigation ──────────────────────────────────────────────────────

    private void showMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        paused = false;
        gameRoot = null;
        gameScene = null;
        primaryStage.setScene(new MenuScene(this::startGame, this::showHelpFromMenu, Platform::exit).getScene());
    }

    private void showHelpFromMenu() {
        primaryStage.setScene(new HelpScene(this::showMenu).getScene());
    }

    /** Opens How to Play while the game is paused. Back returns to the paused game. */
    private void showHelpFromPause() {
        primaryStage.setScene(new HelpScene(() -> primaryStage.setScene(gameScene)).getScene());
    }

    private void startGame() {
        paused = false;
        pauseOverlay = null;

        // ── Canvas ──────────────────────────────────────────────────────────
        Canvas canvas = new Canvas(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Layout ──────────────────────────────────────────────────────────
        gameRoot = new StackPane(canvas);
        gameScene = new Scene(gameRoot, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // ── Input Manager ───────────────────────────────────────────────────
        InputManager inputManager = new InputManager();
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (!paused) showPause();
                else resumeGame();
                return;
            }
            inputManager.onKeyPressed(e.getCode());
        });
        gameScene.setOnKeyReleased(e -> inputManager.onKeyReleased(e.getCode()));
        gameScene.setOnMouseMoved(e -> inputManager.onMouseMoved(e.getX()));

        // ── Game Loop ───────────────────────────────────────────────────────
        gameLoop = new GameLoop(canvas, inputManager);
        gameLoop.start();

        primaryStage.setScene(gameScene);
    }

    // ── Pause management ──────────────────────────────────────────────────────

    private void showPause() {
        paused = true;
        gameLoop.pause();
        pauseOverlay = new PauseOverlay(this::resumeGame, this::startGame, this::showHelpFromPause, Platform::exit);
        gameRoot.getChildren().add(pauseOverlay);
    }

    private void resumeGame() {
        paused = false;
        if (pauseOverlay != null) {
            gameRoot.getChildren().remove(pauseOverlay);
            pauseOverlay = null;
        }
        primaryStage.setScene(gameScene); // restore game scene if coming back from help
        gameLoop.resume();
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        launch(args);
    }
}

