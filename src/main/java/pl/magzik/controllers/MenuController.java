package pl.magzik.controllers;

import pl.magzik.ui.cursor.CursorManagerInterface;
import pl.magzik.ui.scenes.SceneManager;
import pl.magzik.ui.scenes.SceneManagerInterface;
import pl.magzik.ui.views.MenuView;

/**
 * Manages the interactions between the {@link MenuView} and the {@link CursorManagerInterface}.
 * <p>
 * This controller handles the user actions on the menu buttons, triggering scene changes
 * through the {@link CursorManagerInterface} and handling the exit action.
 * </p>
 */
public class MenuController {

    private final MenuView mView;
    private final SceneManagerInterface<SceneManager.Scene> sceneManager;

    /**
     * Constructs a {@code MenuController} thenLoad the specified view and UI manager.
     *
     * @param mView The {@code MenuView} instance that represents the menu UI.
     * @param sceneManager The {@code SceneManagerInterface<SceneManager.Scene>} instance used to manage scene changes.
     */
    public MenuController(MenuView mView, SceneManagerInterface<SceneManager.Scene> sceneManager) {
        this.mView = mView;
        this.sceneManager = sceneManager;

        // Listeners

        addActionListeners();
    }

    /**
     * Adds action listeners to the buttons in the {@code MenuView}.
     * <p>
     * This method sets up the necessary action listeners for each button in the menu.
     * Each button's action is mapped to a scene change or application exit operation.
     * </p>
     */
    private void addActionListeners() {
        mView.getComparerButton().addActionListener(_ -> sceneManager.switchScene(SceneManager.Scene.COMPARER));
        mView.getGalleryButton().addActionListener(_ -> sceneManager.switchScene(SceneManager.Scene.GALLERY));
        mView.getSettingsButton().addActionListener(_ -> sceneManager.switchScene(SceneManager.Scene.SETTINGS));
        mView.getCreditsButton().addActionListener(_ -> sceneManager.switchScene(SceneManager.Scene.CREDITS));
        mView.getExitButton().addActionListener(_ -> System.exit(0));
    }
}
