package pl.magzik.controllers;

import pl.magzik.base.async.ExecutorServiceManager;
import pl.magzik.modules.comparer.ComparerCoordinator;
import pl.magzik.modules.comparer.processing.ComparerModule;
import pl.magzik.ui.cursor.CursorManagerInterface;
import pl.magzik.ui.localization.TranslationStrategy;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.ComparerView;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Controller that manages interactions between the view, comparer logic, and UI components.
 * It handles user inputs, performs background tasks asynchronously, and updates the UI accordingly.
 *
 * <h3>Key Functions:</h3>
 * <ul>
 *   <li>Initializes UI components and their respective action listeners</li>
 *   <li>Uses {@link ExecutorService} to manage background tasks (e.g., file loading, comparison, and moving)</li>
 *   <li>Provides methods to handle file operations (e.g., loading files, comparing them, and resetting states)</li>
 *   <li>Ensures smooth UI updates, error handling, and user experience</li>
 * </ul>
 *
 * <p>Note: The constructor expects non-null parameters, and will throw a {@link NullPointerException} if any are null.</p>
 *
 * @see ComparerView
 * @see ComparerModule
 * @see TranslationStrategy
 * @see MessageInterface
 * @see CursorManagerInterface
 * @see ExecutorService
 */
public class ComparerController {

    private final ComparerCoordinator cc;
    private final ComparerView cView;
    private final TranslationStrategy ti;
    private final MessageInterface mi;
    private final CursorManagerInterface umi;

    /**
     * Initializes a new controller, setting up the view, translation, and logging services.
     * <ul>
     *   <li>Initializes UI list models</li>
     *   <li>Registers action listeners for UI components</li>
     *   <li>Sets up the background task executor from {@link ExecutorServiceManager}</li>
     * </ul>
     *
     * @param cc   The comparer coordinator handling file comparison logic
     * @param cView The view for rendering the UI
     * @param ti   The translation service for internationalization
     * @param mi   The message service for displaying user messages
     * @param umi  The cursor manager for managing UI states
     */
    public ComparerController(ComparerCoordinator cc, ComparerView cView, TranslationStrategy ti, MessageInterface mi, CursorManagerInterface umi) {
        this.cc = cc;

        this.cView = cView;
        this.ti = ti;
        this.mi = mi;
        this.umi = umi;

        // Initialize

        initializeListModel(cc.getListModel("Output"), cView.getFoundList());
        initializeListModel(cc.getListModel("Duplicates"), cView.getDuplicateList());

        // Listeners

        addListeners();
    }

    /**
     * Registers action listeners for the buttons in the view:
     * <ul>
     *   <li>{@code PathButton}: Opens a file chooser for selecting file paths.</li>
     *   <li>{@code LoadButton}: Initiates file loading if a valid path is provided.</li>
     *   <li>{@code MoveButton}: Moves files if comparison results are available.</li>
     *   <li>{@code ResetButton}: Resets the application state.</li>
     * </ul>
     */
    private void addListeners() {
        cView.getPathButton().addActionListener(_ -> handlePathButtonClick());
        cView.getLoadButton().addActionListener(_ -> handleLoadButtonClick());
        cView.getMoveButton().addActionListener(_ -> handleMoveButtonClick());
        cView.getResetButton().addActionListener(_ -> handleResetButtonClick());

        cc.addPropertyChangeListener(cView);
    }

    /**
     * Handles the click event of the path button, opens a file chooser,
     * and updates the comparer module with the selected file path.
     */
    private void handlePathButtonClick() {
        if (cView.getFileChooser().perform()) {
            String path = cView.getPathTextField().getText();
            cc.setInput(path);
        }
    }

    /**
     * Handles the click event of the load button, validates if a file path is set,
     * and initiates the file loading task.
     */
    private void handleLoadButtonClick() {
        String path = cView.getPathTextField().getText();
        if (path == null || path.isEmpty()) {
            mi.showErrorMessage(
                ti.translate("error.comparer.lack_of_images.desc"),
                ti.translate("error.general.title")
            );
            return;
        }

        loadTask();
    }

    /**
     * Handles the move button click, verifies if any comparison results are available,
     * and initiates the file-moving task.
     */
    private void handleMoveButtonClick() {
        if (cc.getOutput().isEmpty()) {
            mi.showErrorMessage(
                ti.translate("error.comparer.loading_needed.desc"),
                ti.translate("error.general.title")
            );
            return;
        }

        moveTask();
    }

    /**
     * Handles the reset button click, clears the UI, resets the module state,
     * and updates the application state to its initial form.
     */
    private void handleResetButtonClick() {
        cView.clear();
        cc.handleClearList("Output");
        cc.handleClearList("Duplicates");
        cc.notifyUnlock();

        cView.getLoadButton().setEnabled(true);
        cView.getPathButton().setEnabled(true);
        cView.getResetButton().setEnabled(false);
        cView.getMoveButton().setEnabled(false);
        cView.getStatusLabel().setText(ti.translate("comparer.state.ready"));
    }

    /**
     * Sets up the UIs list models for rendering file lists.
     * It sets the provided model to the specified {@link JList}.
     *
     * @param model The list model to associate with the JList.
     * @param list  The JList to initialize with the model.
     */
    private void initializeListModel(ListModel<String> model, JList<String> list) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(list);

        list.setModel(model);
    }

    /**
     * Unlocks the button panel and updates the UI state after the task is done.
     */
    private void unlockButtonPanel() {
        cView.getResetButton().setEnabled(true);
        cView.getStatusLabel().setText(ti.translate("comparer.state.done"));
        umi.useCursor(CursorManagerInterface.DEFAULT_CURSOR);
    }

    // Task methods.
    // Handling async tasks and long operations.

    /**
     * Initiates the file loading task, including UI preparation, file loading,
     * and updating after completion.
     */
    private void loadTask() {
        cc.execute(
            () -> prepareUiBefore("comparer.state.prepare"),
            cc::handleLoadFiles,
            () -> updateUiAfter("comparer.state.map", "Output", cc.getInput()),
            cc::handleCompare,
            () -> updateUiAfter("comparer.state.update", "Duplicates", cc.getOutput())
        ).exceptionally(this::handleException)
        .whenComplete((_, _) -> handleLoadTaskCompletion());
    }

    /**
     * Initiates the move files task, including UI preparation and
     * asking for confirmation to restart the comparer.
     */
    private void moveTask() {
        cc.execute(
            () -> prepareUiBefore("comparer.state.move"),
            cc::handleMoveFiles
        ).thenComposeAsync(_ -> restartComparerQuestion())
        .exceptionally(ex -> {
            handleException(ex);
            return null;
        })
        .whenComplete((res, _) -> handleComparerReset(res));
    }

    // UI-related methods.
    // Handling UI-related operations while doing tasks.

    /**
     * Prepares the UI before a task by updating the state label, disabling buttons,
     * and changing the cursor.
     *
     * @param state The translation key to update the state label.
     */
    private void prepareUiBefore(String state) {
        SwingUtilities.invokeLater(() -> {
            cView.blockDestructiveButtons();
            cc.notifyLock();
            umi.useCursor(CursorManagerInterface.WAIT_CURSOR);
            cView.getStatusLabel().setText(ti.translate(state));
        });
    }

    /**
     * Updates the UI after a task, including updating list models and the status label.
     *
     * @param state     The translation key for updating the status label.
     * @param modelName The name of the list model to update.
     * @param sources   The list of files to display in the list model.
     */
    private void updateUiAfter(String state, String modelName, List<File> sources) {
        int total = cc.getInput().size(),
            duplicates = cc.getOutput() == null ? 0 : cc.getOutput().size();

        SwingUtilities.invokeLater(() -> {
            cc.handleFulfilList(modelName, sources);
            cView.updateTray(total, duplicates);
            cView.getStatusLabel().setText(ti.translate(state));
        });
    }

    /**
     * Asks the user if they want to restart the comparer after moving files.
     * Returns a future based on the user's decision.
     *
     * @return A CompletableFuture that completes with {@code true} if the user confirmed the restart.
     */
    private CompletableFuture<Boolean> restartComparerQuestion() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        SwingUtilities.invokeLater(() -> {
            unlockButtonPanel();

            int option = mi.showConfirmationMessage(
                ti.translate("message.confirmation.comparer_restart.desc"),
                ti.translate("message.confirmation.title")
            );

            future.complete(option == JOptionPane.OK_OPTION);
        });

        return future;
    }

    /**
     * Handles the UI updates after completing the load task, enabling relevant buttons and
     * unlocking the UI.
     */
    private void handleLoadTaskCompletion() {
        SwingUtilities.invokeLater(() -> {
            unlockButtonPanel();

            if (!cc.getOutput().isEmpty())
                cView.getMoveButton().setEnabled(true);
        });
    }

    /**
     * Handles the comparer reset based on the user's decision after moving files.
     *
     * @param isReset {@code true} if the user confirmed the comparer reset.
     */
    private void handleComparerReset(boolean isReset) {
        if (!isReset) return;
        SwingUtilities.invokeLater(() -> cView.getResetButton().doClick());
    }

    // Additional methods.
    // E.g., handling exceptions.

    /**
     * Handles exceptions during task execution by logging the error and
     * showing a user-friendly error message.
     *
     * @param ex The thrown exception.
     * @return {@code null} to allow the flow to proceed.
     */
    private Void handleException(Throwable ex) {
        SwingUtilities.invokeLater(() -> mi.showErrorMessage(
            ti.translate("error.general.desc"),
            ti.translate("error.general.title"),
            (Exception) ex.getCause()
        ));

        return null;
    }
}
