package pl.magzik.ui.scenes;

import javax.swing.*;
import java.util.*;

/**
 * Manages the switching of scenes in a {@link JFrame}.
 * <p>
 * This class is responsible for managing a collection of {@link JPanel} scenes and handling the switching between these
 * scenes within a {@link JFrame}. It provides methods to add scenes to the manager and switch the currently displayed
 * scene.
 * </p>
 */
public class SceneManager implements SceneManagerInterface<SceneManager.Scene> {
    private final Map<Scene, JPanel> scenes;
    private final JFrame frame;

    /**
     * Constructs a new {@code SceneManager} for the specified {@link JFrame}.
     *
     * @param frame the {@link JFrame} where scenes will be managed. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code frame} is {@code null}.
     */
    public SceneManager(JFrame frame) {
        Objects.requireNonNull(frame);

        this.frame = frame;
        this.scenes = new HashMap<>();
    }

    /**
     * Adds a scene to the manager's list of scenes.
     * <p>
     * The scene will be available to be switched to later using the {@link #switchScene(Scene)} method. Duplicate scenes
     * can be added, but switching to the same scene multiple times will have the same effect as switching to it once.
     * </p>
     *
     * @param panel the {@link JPanel} scene to add. Must not be {@code null}.
     * @param scene the {@link Scene} identifier for the panel. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code panel} or {@code scene} is {@code null}.
     */
    public void addScene(Scene scene, JPanel panel) {
        Objects.requireNonNull(panel);
        Objects.requireNonNull(scene);

        scenes.put(scene, panel);
    }

    /**
     * Returns a collection of all scenes currently managed by this {@code SceneManager}.
     * <p>
     * This method provides access to the collection of {@link JPanel} scenes that have been added
     * to the {@code SceneManager}. The returned collection is a view of the scenes that are currently
     * available for switching. Note that modifications to the returned collection will not affect
     * the internal state of the {@code SceneManager}.
     * </p>
     *
     * @return a {@link Collection} of {@link JPanel} instances representing the scenes.
     *         This collection is a view of the internal map's values and is not modifiable.
     */
    public Collection<JPanel> getScenes() {
        return scenes.values();
    }

    /**
     * Switches the currently displayed scene to the specified scene.
     * <p>
     * This method removes all previously added scenes from the {@link JFrame} and adds the specified scene. After adding
     * the new scene, it repaints and revalidates the frame to reflect the changes.
     * </p>
     *
     * @param scene the {@link Scene} identifier for the scene to switch to. Must not be {@code null}.
     * @throws IllegalArgumentException if {@code scene} is {@code null}.
     */
    @Override
    public void switchScene(Scene scene) {
        Objects.requireNonNull(scene);

        scenes.values().forEach(frame::remove);
        frame.add(scenes.get(scene));
        frame.repaint();
        frame.revalidate();
    }

    /**
     * Enumeration of possible scenes.
     */
    public enum Scene {
        SETTINGS, COMPARER, GALLERY, MENU, CREDITS
    }
}
