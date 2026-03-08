# Egg Catcher — JavaFX Edition

A 2D "catch the falling eggs" game built with **JavaFX 17** and **Gradle**.

Three types of birds (Chicken, Duck, Muscovy duck) move horizontally across the top of the screen. They randomly drop **eggs** (catch for +1 score) and **droppings** (avoid or lose a life). The player controls a basket at the bottom with the keyboard or mouse. Missing an egg that falls off-screen also costs a life. The game ends when all lives are lost.

## Prerequisites

- **Java 17** or later (JDK, not just JRE)
- No Gradle installation needed — the project includes a [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)

## Quick Start

```bash
# Clone / navigate to the project directory
cd javafx-egg-catcher

# Run the game (Windows)
.\gradlew.bat run

# Run the game (macOS / Linux)
./gradlew run

# Build a JAR
.\gradlew.bat build

# Build a fat-JAR
.\gradlew.bat shadowJar
```

## Controls

| Input           | Action                          |
| --------------- | ------------------------------- |
| ← / → Arrows   | Move basket left / right        |
| Mouse movement  | Move basket to pointer position |
| G               | Spawn an additional Chicken     |
| V               | Spawn an additional Duck        |
| N               | Spawn an additional Muscovy     |
| Enter           | Restart game (on game-over)     |

## Gameplay

- **Score:** +1 for each egg caught
- **Lives:** Start with 3. Lost by catching droppings or missing eggs
- **Level:** Increases every 20 points. Each level makes objects fall faster
- **Birds:** Spawn more birds with keyboard shortcuts to increase difficulty

## Project Structure

```
javafx-egg-catcher/
├── build.gradle                    # Gradle build with JavaFX plugin
├── settings.gradle                 # Root project name
├── gradlew / gradlew.bat          # Gradle wrapper scripts
├── gradle/wrapper/                 # Gradle wrapper JAR + properties
├── README.md                       # This file
├── docs/
│   ├── ARCHITECTURE.md             # Architecture overview & class diagram
│   └── ISSUES-RESOLVED.md          # How original code review issues were fixed
└── src/main/
    ├── java/
    │   ├── module-info.java        # Java module descriptor
    │   └── com/eggcatcher/game/
    │       ├── EggCatcherApp.java  # Application entry point (JavaFX Application)
    │       ├── GameConfig.java     # All constants and bird configurations
    │       ├── GameLoop.java       # AnimationTimer-based game loop
    │       ├── state/
    │       │   └── GameState.java  # Score, lives, level, game-over state
    │       ├── entities/
    │       │   ├── Bird.java       # Abstract base class for all birds
    │       │   ├── Chicken.java    # Bird subclass — fast layer, 10% shit
    │       │   ├── Duck.java       # Bird subclass — medium layer, 25% shit
    │       │   ├── Muscovy.java    # Bird subclass — slow layer, 10% shit
    │       │   ├── FallingObject.java # Abstract base for eggs/droppings
    │       │   ├── Egg.java        # Falling collectible (+1 score)
    │       │   ├── Shit.java       # Falling hazard (-1 life)
    │       │   └── Basket.java     # Player-controlled entity
    │       ├── systems/
    │       │   ├── CollisionSystem.java  # AABB collision detection
    │       │   ├── InputManager.java     # Keyboard + mouse state tracker
    │       │   └── SpawnSystem.java      # Runtime bird spawning
    │       └── rendering/
    │           └── GameRenderer.java     # Canvas rendering + HUD
    └── resources/
        └── images/                 # Game sprites and backgrounds
            ├── background.jpg
            ├── gameover.jpg
            ├── chicken.png
            ├── duck.png
            ├── muscovy.png
            ├── basket.png
            ├── egg.png
            └── shit.png
```

## Technology

| Component    | Technology            |
| ------------ | --------------------- |
| Language     | Java 17               |
| UI Framework | JavaFX 17             |
| Rendering    | Canvas + GraphicsContext |
| Game Loop    | AnimationTimer (~60 FPS) |
| Build        | Gradle 8.6 (wrapper)  |

## License

Educational project — originally by a Fresher Dev, refactored and migrated to JavaFX.
