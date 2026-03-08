package com.eggcatcher.game;

/**
 * Fat-JAR entry point — intentionally does NOT extend {@link javafx.application.Application}.
 * <p>
 * When the JVM's {@code Main-Class} manifest attribute points to a class that
 * extends {@code Application}, JavaFX checks whether its modules are present on
 * the <em>module path</em>. A fat JAR (shadow JAR) runs on the classpath, so
 * that check fails with "JavaFX runtime components are missing", even though all
 * the JavaFX bytecode and native libraries are bundled inside the JAR.
 * </p>
 * <p>
 * Pointing the manifest at this plain class bypasses the check. It immediately
 * delegates to {@link EggCatcherApp#main}, which calls {@code Application.launch()}
 * exactly as normal.
 * </p>
 */
public class Launcher {
    public static void main(String[] args) {
        EggCatcherApp.main(args);
    }
}
