package pl.magzik.controllers;

import pl.magzik.ui.components.Utility;
import pl.magzik.ui.interfaces.UiManagerInterface;
import pl.magzik.ui.views.MenuView;

/**
 * Manages the interactions between the {@link MenuView} and the {@link UiManagerInterface}.
 * <p>
 * This controller handles the user actions on the menu buttons, triggering scene changes
 * through the {@link UiManagerInterface} and handling the exit action.
 * </p>
 */
public class MenuController {

    private final MenuView mView;
    private final UiManagerInterface uiManager;

    /**
     * Constructs a {@code MenuController} with the specified view and UI manager.
     *
     * @param mView The {@code MenuView} instance that represents the menu UI.
     * @param uiManager The {@code UiManagerInterface} instance used to manage scene changes.
     */
    public MenuController(MenuView mView, UiManagerInterface uiManager) {
        this.mView = mView;
        this.uiManager = uiManager;

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
        mView.getComparerButton().addActionListener(_ -> uiManager.toggleScene(Utility.Scene.COMPARER));
        mView.getGalleryButton().addActionListener(_ -> uiManager.toggleScene(Utility.Scene.GALLERY));
        mView.getSettingsButton().addActionListener(_ -> uiManager.toggleScene(Utility.Scene.SETTINGS));
        mView.getCreditsButton().addActionListener(_ -> uiManager.toggleScene(Utility.Scene.CREDITS));
        mView.getExitButton().addActionListener(_ -> System.exit(0));
    }
}
