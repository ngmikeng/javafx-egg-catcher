/**
 * Egg Catcher game module — a JavaFX 2D game where players catch falling
 * eggs with a basket while avoiding droppings.
 */
module com.eggcatcher.game {
    requires javafx.controls;
    requires javafx.graphics;

    exports com.eggcatcher.game;
    exports com.eggcatcher.game.entities;
    exports com.eggcatcher.game.state;
    exports com.eggcatcher.game.systems;
    exports com.eggcatcher.game.rendering;
}
