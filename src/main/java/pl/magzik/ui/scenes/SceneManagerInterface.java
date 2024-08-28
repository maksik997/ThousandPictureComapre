package pl.magzik.ui.scenes;

/**
 * Interface for managing scenes in a user interface.
 * <p>
 * This interface defines a method for switching between different scenes or panels in a user interface.
 * </p>
 *
 * @param <T> the type of panel or scene to be managed.
 */
@FunctionalInterface
public interface SceneManagerInterface <T> {
    /**
     * Switches the current scene to the specified panel.
     * <p>
     * This method removes the currently displayed scene and adds the new panel. After switching, it repaints
     * and revalidates the frame to ensure that the new scene is properly displayed.
     * </p>
     *
     * @param panel the panel or scene to switch to. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code panel} is {@code null}.
     */
    void switchScene(T panel);
}
