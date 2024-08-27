package pl.magzik;

import pl.magzik.controllers.*;

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
public class Controller {

    private final View view;
    private final Model model;
    private final ResourceBundle resourceBundle;
    private final MenuController menuController;
    private final ComparerController comparerController;
    private final GalleryController galleryController;
    private final SettingsController settingsController;
    private final TranslationController translationController;

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
        this.translationController = new TranslationController(resourceBundle);
        this.menuController = new MenuController(view.getMenuView(), view);
        this.comparerController = new ComparerController(view.getComparerView(), model.getComparerModule(), translationController, view, view);
        this.galleryController = new GalleryController(view.getGalleryView(), model.getGalleryModule(), view, view, translationController);
        this.settingsController = new SettingsController(view.getSettingsView(), model.getSettingsModule(), model.getGalleryModule(), translationController, view, model.getComparerModule(), model.getGalleryModule());

        // Translate Components Post-construct
        view.translateComponents();
        view.translateComponents(model.getGalleryModule().getGalleryTableModel());
    }

}
