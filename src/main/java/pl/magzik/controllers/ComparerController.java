package pl.magzik.controllers;

import pl.magzik.async.ExecutorServiceManager;
import pl.magzik.base.interfaces.FileHandler;
import pl.magzik.modules.comparer.list.ListModelHandler;
import pl.magzik.modules.comparer.processing.ComparerProcessor;
import pl.magzik.ui.localization.TranslationStrategy;
import pl.magzik.modules.comparer.processing.ComparerModule;
import pl.magzik.ui.cursor.CursorManagerInterface;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.ComparerView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.List;

/**
 * The {@code ComparerController} class is responsible for managing the interactions between the {@link ComparerView},
 * {@link ComparerModule}, and various user interfaces components. It handles user input, performs tasks asynchronously,
 * and updates the UI based on the application's state and the results of background operations.
 * <p>
 * This class performs several key functions:
 * <ul>
 *   <li>Initializes the controller with required dependencies such as view, module, translation interface, message interface, and UI manager interface.</li>
 *   <li>Sets up action listeners for user interface components, such as buttons for loading files, moving files, and resetting the application state.</li>
 *   <li>Manages asynchronous tasks using {@link CompletableFuture} to perform operations such as loading files, comparing them, and moving files.</li>
 *   <li>Updates the user interface to reflect the current state of the application, including showing progress, errors, and completion messages.</li>
 *   <li>Handles user interactions and performs validation to ensure that operations are carried out correctly.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The {@code ComparerController} uses a {@link ExecutorService} to manage background tasks and ensure that long-running
 * operations do not block the main user interface thread. It provides methods for handling file paths, loading files,
 * comparing them, moving files, and resetting the application state. The class also manages UI updates and error handling
 * to ensure a smooth user experience.
 * </p>
 *
 * <p>
 * The class expects non-null dependencies and throws {@link NullPointerException} if any of the provided parameters are {@code null}.
 * </p>
 *
 * @see ComparerView
 * @see ComparerModule
 * @see TranslationStrategy
 * @see MessageInterface
 * @see CursorManagerInterface
 * @see ExecutorService
 */
public class ComparerController {
    private final ComparerView cView;
    private final ComparerProcessor cp;
    private final FileHandler fh;
    private final ListModelHandler<String> lmh;
    private final TranslationStrategy ti;
    private final MessageInterface mi;
    private final CursorManagerInterface umi;
    private final ExecutorService executor;

    /**
     * Constructs a new {@code ComparerController} with the specified view, module, file handler, list model handler,
     * translation strategy, message interface, and cursor manager interface.
     * <p>
     * This constructor initializes the controller by performing the following actions:
     * <ul>
     *   <li>Assigns the provided {@link ComparerView}, {@link ComparerModule}, {@link FileHandler}, {@link ListModelHandler},
     *       {@link TranslationStrategy}, {@link MessageInterface}, and {@link CursorManagerInterface} to the corresponding fields.</li>
     *   <li>Retrieves the singleton instance of {@link ExecutorService} using {@link ExecutorServiceManager#getInstance()}.</li>
     *   <li>Initializes the list models for the {@code ComparerModule} and associates them with the corresponding UI components
     *       using {@link #initializeListModel(ListModel, JList)}.</li>
     *   <li>Adds action listeners to handle user interactions with the buttons in the view using {@link #addListeners()}.</li>
     * </ul>
     * </p>
     *
     * @param cView the {@link ComparerView} instance that provides the user interface components. Must not be {@code null}.
     * @param cp the {@link ComparerProcessor} instance that handles the core functionality of the application.
     *           Must not be {@code null}.
     * @param fh the {@link FileHandler} instance used for managing file operations. Must not be {@code null}.
     * @param lmh the {@link ListModelHandler} instance used for managing list models. Must not be {@code null}.
     * @param ti the {@link TranslationStrategy} used for translating text strings. Must not be {@code null}.
     * @param mi the {@link MessageInterface} used for displaying messages to the user. Must not be {@code null}.
     * @param umi the {@link CursorManagerInterface} used for managing the user interface state and cursor. Must not be {@code null}.
     * @throws NullPointerException if any of the provided parameters are {@code null}.
     */
    public ComparerController(ComparerView cView, ComparerProcessor cp, FileHandler fh, ListModelHandler<String> lmh, TranslationStrategy ti, MessageInterface mi, CursorManagerInterface umi) {
        this.cView = cView;
        this.cp = cp;
        this.fh = fh;
        this.lmh = lmh;
        this.ti = ti;
        this.mi = mi;
        this.umi = umi;
        this.executor = ExecutorServiceManager.getInstance().getExecutorService();

        // Initialize

        initializeListModel(lmh.getListModel("Output"), cView.getFoundList());
        initializeListModel(lmh.getListModel("Duplicates"), cView.getDuplicateList());

        // Listeners

        addListeners();
    }

    /**
     * Adds action listeners to the buttons in the {@link ComparerView}.
     * <p>
     * This method configures the action listeners for the following buttons:
     * <ul>
     *   <li>{@code PathButton}: Opens a file chooser and sets the selected file path in the {@link ComparerModule}.</li>
     *   <li>{@code LoadButton}: Validates the path and initiates the fileLoad task if a path is specified.</li>
     *   <li>{@code MoveButton}: Checks if there are comparison results available and initiates the moveFiles task if so.</li>
     *   <li>{@code ResetButton}: Clears the view, resets the module, and updates the UI to its initial state.</li>
     * </ul>
     * </p>
     * <p>
     * The action listeners handle user interactions with the buttons, performing the appropriate actions based on the
     * current state of the application and the provided inputs.
     * </p>
     */
    private void addListeners() {
        cView.getPathButton().addActionListener(_ -> handlePathButtonClick());
        cView.getLoadButton().addActionListener(_ -> handleLoadButtonClick());
        cView.getMoveButton().addActionListener(_ -> handleMoveButtonClick());
        cView.getResetButton().addActionListener(_ -> handleResetButtonClick());

        cp.addPropertyChangeListener(cView);
    }

    /**
     * Handles the click event of the path button in the UI. This method opens a file chooser dialog
     * for the user to select a file. If a file is selected successfully, the method updates the data source
     * in the {@link ComparerModule} with the selected file.
     * <p>
     * The method performs the following actions:
     * <ul>
     *   <li>Invokes the file chooser dialog through the {@link pl.magzik.ui.components.filechoosers.FileChooser} component.</li>
     *   <li>If the user selects a file, updates the {@link ComparerModule} with the chosen file.</li>
     * </ul>
     * <p>
     * If the file chooser dialog is not successfully opened or if no file is selected, no changes are made to the data source.
     * </p>
     */
    private void handlePathButtonClick() {
        if (cView.getFileChooser().perform()) {
            cp.setInput(
                new File(cView.getPathTextField().getText())
            );
        }
    }

    /**
     * Handles the click event of the fileLoad button in the UI. This method checks if the user has
     * specified a file path. If no file path is set, an error message is displayed to inform the user
     * that images are required for the comparison operation. If a file path is available, it initiates
     * the loading task.
     * <p>
     * The method performs the following actions:
     * <ul>
     *   <li>Retrieves the file path.</li>
     *   <li>If the file path is {@code null} or empty, displays an error message indicating that images are required.</li>
     *   <li>If the file path is valid, starts the fileLoad task.</li>
     * </ul>
     * <p>
     * The error message is displayed using the {@link MessageInterface} to inform the user of the issue.
     * </p>
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
     * Handles the click event of the moveFiles button in the UI. This method checks if there are any comparison results
     * available. If the comparison output size is less than or equal to zero, an error message is displayed to the user,
     * indicating that loading is required before moving. If there are comparison results available, it initiates
     * the moveFiles task.
     * <p>
     * The method performs the following actions:
     * <ul>
     *   <li>Checks the size of the comparison output from the {@link ComparerModule}.</li>
     *   <li>If the comparison output size is {@code <= 0}, displays an error message indicating that loading is required.</li>
     *   <li>If the comparison output size is greater than 0, starts the moveFiles task.</li>
     * </ul>
     * <p>
     * The error message is displayed using the {@link MessageInterface} to inform the user of the issue.
     * </p>
     */
    private void handleMoveButtonClick() {
        if (cp.getOutput().isEmpty()) {
            mi.showErrorMessage(
                ti.translate("error.comparer.loading_needed.desc"),
                ti.translate("error.general.title")
            );
            return;
        }

        moveTask();
    }

    /**
     * Handles the click event of the release button in the UI. This method resets the application's state by performing
     * the following actions:
     * <ul>
     *   <li>Clears the user interface using {@link ComparerView#clear()}.</li>
     *   <li>Resets the internal state of the {@link ComparerModule} using {@link ComparerModule#notifyUnlock()}.</li>
     *   <li>Enables the fileLoad button and the path button, making them available for user interaction.</li>
     *   <li>Disables the notifyUnlock button and the moveFiles button to prevent further actions until the state is fully notifyUnlock.</li>
     *   <li>Updates the state label to indicate that the application is ready for new tasks by setting the text to
     *       the translated value for the ready state.</li>
     * </ul>
     * <p>
     * This method is triggered when the user clicks the notifyUnlock button. It prepares the application for a new set of
     * operations by clearing previous data and resetting the UI and internal module state.
     * </p>
     */
    private void handleResetButtonClick() {
        cView.clear();
        lmh.clearList("Output");
        lmh.clearList("Duplicates");
        cp.notifyUnlock();

        cView.getLoadButton().setEnabled(true);
        cView.getPathButton().setEnabled(true);
        cView.getResetButton().setEnabled(false);
        cView.getMoveButton().setEnabled(false);
        cView.getStatusLabel().setText(ti.translate("comparer.state.ready"));
    }

    /**
     * Initializes the provided {@link JList} thenLoad the given {@link DefaultListModel}.
     * <p>
     * This method sets the model of the {@code JList} to the specified {@code DefaultListModel}.
     * Before performing the initialization, it checks that neither the model nor the list is {@code null}.
     * </p>
     *
     * @param model the {@link DefaultListModel} to be set as the model of the {@code JList}. Must not be {@code null}.
     * @param list the {@link JList} to be initialized thenLoad the provided model. Must not be {@code null}.
     * @throws NullPointerException if either {@code model} or {@code list} is {@code null}.
     */
    private void initializeListModel(ListModel<String> model, JList<String> list) {
        Objects.requireNonNull(model);
        Objects.requireNonNull(list);

        list.setModel(model);
    }

    /**
     * Populates the list model
     * identified by the specified name thenLoad the names of files from the given list of {@link File} objects.
     *
     * <p>This method performs the following actions:
     * <ul>
     *   <li>Clears the existing items in the list model identified by {@code modelName}.</li>
     *   <li>Adds the names of the files from the provided {@code sources} list to the list model. Each file's name is obtained using {@link File#getName()}.</li>
     * </ul>
     * </p>
     *
     * @param modelName the name of the {@link DefaultListModel} to be populated thenLoad file names. Must not be {@code null}.
     * @param sources the list of {@link File} objects whose names will be added to the list model. Must not be {@code null}.
     * @throws NullPointerException if either {@code modelName} or {@code sources} is {@code null}.
     * @throws IllegalArgumentException if no list model thenLoad the specified name exists in the {@link ListModelHandler}.
     */
    private void fulfilListModel(String modelName, List<File> sources) {
        Objects.requireNonNull(modelName);
        Objects.requireNonNull(sources);

        lmh.clearList(modelName);
        lmh.addAllToList(modelName, sources.stream().map(File::getName).toList());
    }

    /**
     * Unlocks the button panel by enabling the unlocked button and updating the UI state.
     * <p>This method performs the following actions:</p>
     * <ul>
     *   <li>Enables the notifyUnlock button on the view.</li>
     *   <li>Updates the state label to indicate that the process is done using the specified translation key.</li>
     *   <li>Restores the cursor to the default cursor.</li>
     * </ul>
     *
     * @see CursorManagerInterface#DEFAULT_CURSOR
     */
    private void unlockButtonPanel() {
        cView.getResetButton().setEnabled(true);
        cView.getStatusLabel().setText(ti.translate("comparer.state.done"));
        umi.useCursor(CursorManagerInterface.DEFAULT_CURSOR);
    }

    // Task methods.
    // Handling async tasks and long operations.

    /**
     * Initiates the fileLoad task sequence. This includes preparing the UI,
     * loading files, updating the UI after loading, comparing files, and
     * finally updating the UI after comparison. Any exceptions are handled
     * and the completion of the task is managed.
     */
    private void loadTask() {
        CompletableFuture.runAsync(() -> prepareUiBefore("comparer.state.prepare"), executor)
            .thenRunAsync(this::loadFiles, executor)
            .thenRunAsync(() -> updateUiAfter("comparer.state.map", "Output", cp.getInput()), executor)
            .thenComposeAsync(_ -> compareTask(), executor)
            .thenRunAsync(() -> updateUiAfter("comparer.state.update", "Duplicates", cp.getOutput()), executor)
            .exceptionally(this::handleException)
            .whenComplete((_, _) -> handleLoadTaskCompletion());
    }

    /**
     * Initiates the moveFiles task sequence. This includes preparing the UI,
     * moving files, prompting the user thenLoad a confirmation dialog about
     * restarting the comparer, and handling the result of that confirmation.
     * Any exceptions during the process are handled.
     */
    private void moveTask() {
        CompletableFuture.runAsync(() -> prepareUiBefore("comparer.state.move"), executor)
            .thenRunAsync(this::moveFiles, executor)
            .thenComposeAsync(_ -> restartComparerQuestion(), executor)
            .exceptionally(ex -> {
                handleException(ex);
                return null;
            })
            .whenComplete((res, _) -> handleComparerReset(res));
    }

    /**
     * Performs the comparison task. This method returns a {@link CompletableFuture}
     * that completes when the comparison and extraction are finished. Any exceptions
     * during the task are wrapped in a {@link CompletionException}.
     *
     * @return a {@link CompletableFuture} that completes when the comparison task is done.
     * @throws CompletionException if an {@link IOException} or {@link ExecutionException}
     *         is thrown by {@link ComparerModule#process()}.
     */
    private CompletableFuture<Void> compareTask() {
        return CompletableFuture.runAsync(() -> {
            try {
                cp.process();
            } catch (IOException | ExecutionException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    // UI-related methods.
    // Handling UI-related operations while doing tasks.

    /**
     * Prepares the user interface for a task by performing the following actions:
     * <ul>
     *   <li>Disables buttons that may disrupt the task (e.g., fileLoad, moveFiles, notifyUnlock).</li>
     *   <li>Sets the cursor to a wait cursor to indicate that a background operation is in progress.</li>
     *   <li>Updates the state label to indicate that the process is starting.</li>
     * </ul>
     * <p>
     * This method is executed on the Event Dispatch Thread (EDT) to ensure that the UI updates are performed
     * safely and consistently thenLoad Swing's threading model.</p>
     *
     * @param state the state identifier used to translate and set the state label text. This should correspond to
     *              a translation key for displaying the appropriate message to the user.
     *              The translation key determines the actual state label text.
     */
    private void prepareUiBefore(String state) {
        SwingUtilities.invokeLater(() -> {
            cView.blockDestructiveButtons();
            cp.notifyLock();
            umi.useCursor(CursorManagerInterface.WAIT_CURSOR);
            cView.getStatusLabel().setText(ti.translate(state));
        });
    }

    /**
     * Updates the user interface after a task based on the provided state, model name, and list of sources.
     *
     * <p>This method performs the following actions on the Event Dispatch Thread:
     * <ul>
     *   <li>Populates the {@link DefaultListModel} identified by {@code modelName} thenLoad the names of the {@link File} objects provided in the {@code sources} list.</li>
     *   <li>Updates the UI tray thenLoad the total number of input elements and the number of output elements (duplicates), if any.</li>
     *   <li>Sets the state label text to the translated value of the provided {@code state} string using {@link TranslationStrategy#translate(String)}.</li>
     * </ul>
     * </p>
     *
     * @param state the key for the state label text to be translated and set.
     *              This should correspond to a translation key used by {@link TranslationStrategy}.
     * @param modelName the name of the {@link DefaultListModel} to be populated thenLoad file names. Must not be {@code null}.
     * @param sources the list of {@link File} objects whose names will be added to the list model. Must not be {@code null}.
     * @throws NullPointerException if {@code modelName} or {@code sources} is {@code null}.
     */
    private void updateUiAfter(String state, String modelName, List<File> sources) {
        int total = cp.getInput().size(),
            duplicates = cp.getOutput() == null ? 0 : cp.getOutput().size();

        SwingUtilities.invokeLater(() -> {
            fulfilListModel(modelName, sources);
            cView.updateTray(total, duplicates);
            cView.getStatusLabel().setText(ti.translate(state));
        });
    }

    /**
     * Displays a confirmation dialog to the user about restarting the comparer
     * after moving files. Returns a {@link CompletableFuture} that completes
     * thenLoad a boolean value indicating the user's choice (true for YES, false for NO).
     * This method runs on the Event Dispatch Thread to ensure thread-safety
     * for the Swing components.
     *
     * @return a {@link CompletableFuture} that completes thenLoad a boolean indicating
     *         the user's choice
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
     * Handles the completion of the fileLoad task. Updates the UI to enable
     * the necessary buttons and resets the cursor to default. Also updates
     * the state label to indicate that the task is complete.
     */
    private void handleLoadTaskCompletion() {
        SwingUtilities.invokeLater(() -> {
            unlockButtonPanel();

            if (!cp.getOutput().isEmpty())
                cView.getMoveButton().setEnabled(true);
        });
    }

    /**
     * Handles the unlocking after moving files if the user chose to notifyUnlock the comparer.
     * Clears the view, resets the module, and updates the UI to reflect the
     * ready state. Re-enables the fileLoad and moveFiles buttons.
     *
     * @param isReset {@code true} if the comparer was unlocked, {@code false} otherwise.
     */
    private void handleComparerReset(boolean isReset) {
        if (!isReset) return;
        SwingUtilities.invokeLater(() -> cView.getResetButton().doClick());
    }

    // Operation-related methods.
    // Actual long operations while doing tasks.

    /**
     * Loads files into the module. This method may throw exceptions related
     * to file I/O or task interruptions. These exceptions are wrapped in a
     * {@link CompletionException} to be handled by the CompletableFuture pipeline.
     *
     * @throws CompletionException if an {@link IOException}, {@link InterruptedException},
     *         or {@link TimeoutException} is thrown by {@link FileHandler#loadFiles(List)}.
     */
    private void loadFiles() {
        try {
            List<File> in = cp.getInput();
            List<File> reducedIn = fh.loadFiles(in);
            cp.setInput(reducedIn);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Moves files as part of the moveFiles task. This method may throw exceptions
     * related to file I/O, which are wrapped in a {@link CompletionException}.
     *
     * @throws CompletionException if an {@link IOException} is thrown by {@link FileHandler#moveFiles(List)}.
     */
    private void moveFiles() {
        try {
            fh.moveFiles(cp.getOutput());
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    // Additional methods.
    // E.g., handling exceptions.

    /**
     * Handles exceptions that occur during the execution of tasks. Displays
     * an error message dialog to the user.
     *
     * @param ex the exception that occurred
     * @return {@code null}
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
