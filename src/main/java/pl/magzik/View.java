package pl.magzik;

import pl.magzik.ui.UiManager;
import pl.magzik.ui.localization.ComponentTranslationStrategy;
import pl.magzik.ui.scenes.SceneManager;
import pl.magzik.ui.views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Represents the main application window that handles scene management and user interface operations.
 * <p>
 * The {@code View} class extends {@link JFrame} and serves as the primary container for different scenes
 * within the application. It manages various views such as {@link ComparerView}, {@link SettingsView},
 * {@link GalleryView}, {@link MenuView}, and {@link CreditsView}, and provides methods for switching between
 * these scenes. It also integrates translation and UI management functionalities.
 * </p>
 */
public class View extends JFrame {

    private final ComparerView comparerView;
    private final SettingsView settingsView;
    private final GalleryView galleryView;
    private final MenuView menuView;
    private final CreditsView creditsView;
    private final SceneManager sceneManager;
    private final ComponentTranslationStrategy translationStrategy;
    private final UiManager uiManager;

    /**
     * Constructs a new {@code View} instance with the specified views and translation strategy.
     *
     * @param comparerView the {@link ComparerView} instance. Must not be {@code null}.
     * @param settingsView the {@link SettingsView} instance. Must not be {@code null}.
     * @param galleryView the {@link GalleryView} instance. Must not be {@code null}.
     * @param menuView the {@link MenuView} instance. Must not be {@code null}.
     * @param creditsView the {@link CreditsView} instance. Must not be {@code null}.
     * @param translationStrategy the {@link ComponentTranslationStrategy} used for translating UI components. Must not be {@code null}.
     * @throws HeadlessException if the environment does not support a display.
     */
    private View(ComparerView comparerView, SettingsView settingsView, GalleryView galleryView, MenuView menuView, CreditsView creditsView, ComponentTranslationStrategy translationStrategy) throws HeadlessException {
        this.comparerView = comparerView;
        this.settingsView = settingsView;
        this.galleryView = galleryView;
        this.menuView = menuView;
        this.creditsView = creditsView;
        this.sceneManager = new SceneManager(this);
        this.translationStrategy = translationStrategy;
        this.uiManager = new UiManager(this);

        addScenes();
        addListeners();
        setUpFrame();
    }

    /**
     * Adds all scenes to the {@link SceneManager}.
     * <p>
     * This method registers each view with the {@link SceneManager} to manage scene switching.
     * </p>
     */
    private void addScenes() {
        sceneManager.addScene(SceneManager.Scene.GALLERY, galleryView);
        sceneManager.addScene(SceneManager.Scene.SETTINGS, settingsView);
        sceneManager.addScene(SceneManager.Scene.COMPARER, comparerView);
        sceneManager.addScene(SceneManager.Scene.MENU, menuView);
        sceneManager.addScene(SceneManager.Scene.CREDITS, creditsView);
    }

    /**
     * Adds action listeners to all "back" buttons to switch back to the {@link SceneManager.Scene#MENU}.
     * <p>
     * This method sets up a common listener for the "back" buttons in the scenes to return to the menu.
     * </p>
     */
    private void addListeners() {
        ActionListener backButtonListener = _ -> sceneManager.switchScene(SceneManager.Scene.MENU);

        sceneManager.getScenes().stream()
                .filter(p -> p instanceof AbstractView)
                .map(AbstractView.class::cast)
                .forEach(p -> p.getBackButton().addActionListener(backButtonListener));
    }

    /**
     * Configures the main application frame.
     * <p>
     * This method sets the icon, title, minimum size, and default close operation for the {@code View} frame.
     * It also adds the initial {@link MenuView} to the frame.
     * </p>
     */
    private void setUpFrame() {
        ImageIcon icon = new ImageIcon("data/thumbnail_64x64.png");
        setIconImage(icon.getImage());
        setTitle("general.title");
        setMinimumSize(new Dimension(800, 650));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(menuView);
    }

    /**
     * Returns the {@link ComparerView} instance.
     *
     * @return the {@link ComparerView} instance.
     */
    public ComparerView getComparerView() {
        return comparerView;
    }

    /**
     * Returns the {@link SettingsView} instance.
     *
     * @return the {@link SettingsView} instance.
     */
    public SettingsView getSettingsView() {
        return settingsView;
    }

    /**
     * Returns the {@link GalleryView} instance.
     *
     * @return the {@link GalleryView} instance.
     */
    public GalleryView getGalleryView() {
        return galleryView;
    }

    /**
     * Returns the {@link MenuView} instance.
     *
     * @return the {@link MenuView} instance.
     */
    public MenuView getMenuView() {
        return menuView;
    }

    /**
     * Returns the {@link CreditsView} instance.
     *
     * @return the {@link CreditsView} instance.
     */
    public CreditsView getCreditsView() {
        return creditsView;
    }

    /**
     * Returns the {@link SceneManager} instance used for scene management.
     *
     * @return the {@link SceneManager} instance.
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * Returns the {@link ComponentTranslationStrategy} used for translating components.
     *
     * @return the {@link ComponentTranslationStrategy} instance.
     */
    public ComponentTranslationStrategy getTranslationStrategy() {
        return translationStrategy;
    }

    /**
     * Returns the {@link UiManager} instance used for UI operations.
     *
     * @return the {@link UiManager} instance.
     */
    public UiManager getUiManager() {
        return uiManager;
    }

    /**
     * Factory class for creating {@link View} instances.
     * <p>
     * The {@code Factory} class provides a method to create a fully initialized {@link View} instance
     * using the specified {@link ResourceBundle} for localization.
     * </p>
     */
    public static class Factory {

        /**
         * Creates a new {@link View} instance with the provided {@link ResourceBundle}.
         *
         * @param resourceBundle the {@link ResourceBundle} for localization. Must not be {@code null}.
         * @return a new {@link View} instance.
         * @throws IllegalArgumentException if {@code resourceBundle} is {@code null}.
         */
        public static View create(ResourceBundle resourceBundle) {
            Objects.requireNonNull(resourceBundle);

            ComparerView comparerView = ComparerView.Factory.create();
            SettingsView settingsView = SettingsView.Factory.create();
            GalleryView galleryView = GalleryView.Factory.create();
            MenuView menuView = MenuView.Factory.create();
            CreditsView creditsView = new CreditsView();
            ComponentTranslationStrategy translationStrategy = new ComponentTranslationStrategy(resourceBundle);

            return new View(
              comparerView, settingsView,
                galleryView, menuView,
                creditsView, translationStrategy
            );
        }
    }
}
