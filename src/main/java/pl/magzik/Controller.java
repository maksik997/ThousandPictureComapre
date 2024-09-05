package pl.magzik;

import pl.magzik.controllers.ComparerController;
import pl.magzik.controllers.GalleryController;
import pl.magzik.controllers.MenuController;
import pl.magzik.controllers.SettingsController;
import pl.magzik.ui.localization.DefaultTranslationStrategy;
import pl.magzik.ui.localization.TranslationStrategy;

import java.util.ResourceBundle;

/**
 * Main controller for managing the overall application logic.
 * <p>
 * The {@code Controller} class acts as the central hub for coordinating the interactions between
 * the {@link View}, {@link Model}, and various sub-controllers. It initializes the sub-controllers
 * responsible for different parts of the application, such as the menu, comparer, gallery, and settings.
 * The controller also handles the localization of the user interface by translating the components
 * based on the provided {@link ResourceBundle}.
 * </p>
 * <p>
 * This controller should be instantiated after the {@link View} and {@link Model} have been fully constructed,
 * as it relies on them for initialization. The sub-controllers are final and are created during the construction
 * of the main {@code Controller}, ensuring that they are immutable and their dependencies are satisfied.
 * </p>
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"}) // Hurts my eyes :P
public class Controller {

    private final View view;
    private final Model model;
    private final ResourceBundle resourceBundle;
    private final MenuController menuController;
    private final ComparerController comparerController;
    private final GalleryController galleryController;
    private final SettingsController settingsController;
    private final TranslationStrategy translationStrategy;

    /**
     * Constructs the main {@code Controller} for the application.
     * <p>
     * Initializes the primary controllers for the various views and modules in the application,
     * and performs the initial translation of the UI components to the current locale.
     * </p>
     *
     * @param view            the main {@link View} object that represents the UI components.
     * @param model           the {@link Model} object that contains the application's data and business logic.
     * @param resourceBundle  the {@link ResourceBundle} used for localization of UI strings.
     */
    public Controller(View view, Model model, ResourceBundle resourceBundle) {
        this.view = view;
        this.model = model;
        this.resourceBundle = resourceBundle;
        this.translationStrategy = new DefaultTranslationStrategy(resourceBundle);
        this.menuController = new MenuController(view.getMenuView(), view.getSceneManager());
        this.comparerController = new ComparerController(model.getCc(), view.getComparerView(), translationStrategy, view.getUiManager(), view.getUiManager());
        this.galleryController = new GalleryController(model.getGc(), view.getGalleryView(), model.getCc(), view.getUiManager(), view.getUiManager(), translationStrategy);
        this.settingsController = new SettingsController(view.getSettingsView(), model.getSettingsModule(), model.getGc().getGalleryPropertyAccess(), translationStrategy, view.getUiManager(), model.getCc().getComparerPropertyAccess(), model.getCc().getComparerFilePropertyAccess());

        // Translate Components Post-construct
        view.getTranslationStrategy().translateComponents(view);
        view.getTranslationStrategy().translateComponents(model.getGc().getTablePropertyAccess());
    }

}
