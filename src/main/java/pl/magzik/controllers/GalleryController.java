package pl.magzik.controllers;

import pl.magzik.base.async.ExecutorServiceManager;
import pl.magzik.modules.comparer.ComparerCoordinator;
import pl.magzik.modules.gallery.GalleryCoordinator;
import pl.magzik.modules.gallery.table.GalleryTableModel;
import pl.magzik.modules.gallery.table.GalleryTableRowSorter;
import pl.magzik.ui.cursor.CursorManagerInterface;
import pl.magzik.ui.listeners.UnifiedDocumentListener;
import pl.magzik.ui.localization.TranslationStrategy;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.GalleryView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

/**
 * Manages gallery operations such as adding, removing, deleting images, and managing tags.
 * <p>
 * The {@code GalleryController} interacts with the {@link GalleryView}, {@link GalleryCoordinator},
 * {@link ComparerCoordinator}, and various user interface components to perform and manage gallery operations.
 * It utilizes an {@link ExecutorService} to run tasks asynchronously and provides methods for handling
 * various user actions.
 * </p>
 */
public class GalleryController {

    private final GalleryCoordinator gc;
    private final GalleryView gView;
    private final ComparerCoordinator cc;
    private final MessageInterface mi;
    private final CursorManagerInterface umi;
    private final TranslationStrategy ti;
    private final ExecutorService executor;

    /**
     * Constructs a {@code GalleryController} with the specified view, coordinators, and interfaces.
     * <p>
     * Initializes the UI components, adds listeners to the UI elements, and sets up the executor service for
     * asynchronous task execution.
     * </p>
     *
     * @param gc   the {@link GalleryCoordinator} for handling gallery-related operations.
     * @param gView the {@link GalleryView} to interact with the user interface.
     * @param cc    the {@link ComparerCoordinator} to handle comparison operations.
     * @param umi   the {@link CursorManagerInterface} to manage the UI cursor.
     * @param mi    the {@link MessageInterface} to display messages to the user.
     * @param ti    the {@link TranslationStrategy} to translate messages in the UI.
     */
    public GalleryController(GalleryCoordinator gc, GalleryView gView, ComparerCoordinator cc, CursorManagerInterface umi, MessageInterface mi, TranslationStrategy ti) {
        this.gc = gc;
        this.cc = cc;

        this.ti = ti;
        this.umi = umi;
        this.mi = mi;
        this.gView = gView;
        this.executor = ExecutorServiceManager.getInstance().getExecutorService();

        // Initialize

        initialize();

        // Listeners

        addListeners();
    }

    /**
     * Initializes the gallery view thenLoad the necessary parts and settings.
     * <p>
     * Sets the file chooser, table model, row sorter, and updates the element count label.
     * </p>
     */
    private void initialize() {
        gView.setFileChooser(gc);
        gc.assignTableModel(gView.getGalleryTable());
        gView.setGalleryRowSorter(new GalleryTableRowSorter((GalleryTableModel) gc.getTableModel()));
        updateUiAfter();
    }

    /**
     * Adds action listeners to various UI components.
     * <p>
     * Sets up listeners for buttons and text fields to handle user actions like adding, removing,
     * and deleting images, as well as tag management and filtering.
     * </p>
     */
    private void addListeners() {
        gView.getNameFilterTextField().getDocument().addDocumentListener((UnifiedDocumentListener)_ -> handleNameFilterUpdate());
        gView.getAddImageButton().addActionListener(_ -> addImagesTask());
        gView.getRemoveImageButton().addActionListener(_ -> handleRemoveImagesButton());
        gView.getDeleteImageButton().addActionListener(_ -> handleDeleteImagesButton());
        gView.getDistinctButton().addActionListener(_ -> handleDistinctButton());
        gView.getUnifyNamesButton().addActionListener(_ -> unifyNamesTask());
        gView.getOpenButton().addActionListener(_ -> handleOpenButton());
        gView.getAddTagButton().addActionListener(_ -> handleAddTagButton());
        gView.getRemoveTagButton().addActionListener(_ -> handleRemoveTagButton());

        cc.addPropertyChangeListener(gView);
    }

    /**
     * Handles the "Remove" button click to remove selected images.
     * <p>
     * Initiates the remove operation if images are selected.
     * </p>
     */
    private void handleRemoveImagesButton() {
        if (checkIfNotSelected()) return;
        removeImagesTask();
    }

    /**
     * Handles the "Delete" button click to delete selected images.
     * <p>
     * Asks the user for confirmation before starting the deletion process if images are selected.
     * </p>
     */
    private void handleDeleteImagesButton() {
        if (checkIfNotSelected()) return;

        int answer = mi.showConfirmationMessage(
            ti.translate("message.confirmation.delete_images.desc"),
            ti.translate("message.confirmation.delete_images.title")
        );

        if (answer == JOptionPane.YES_OPTION) {
            deleteImagesTask();
        }
    }

    /**
     * Handles the "Distinct" button click to distinct selected images.
     * <p>
     * Initiates the distinct operation if images are selected.
     * </p>
     */
    private void handleDistinctButton() {
        if (checkIfNotSelected()) return;

        distinctImagesTask();
    }

    /**
     * Handles the "Open" button click to open selected images.
     * <p>
     * Opens the selected images in the system's default image viewer. Displays an error if an {@link IOException} occurs.
     * </p>
     */
    private void handleOpenButton() {
        if (checkIfNotSelected()) return;
        List<Integer> selected = gView.getAndClearSelectedRows();

        try {
            gc.handleOpen(selected);
        } catch (IOException e) {
            mi.showErrorMessage(
                ti.translate("error.open.ioexception.desc"),
                ti.translate("error.general.title")
            );
        }
    }

    /**
     * Handles the "Add Tag" button click to add a tag to selected images.
     * <p>
     * Prompts the user to select a tag and adds it to the selected images.
     * </p>
     */
    private void handleAddTagButton() {
        if (checkIfNotSelected()) return;

        String[] tags = gc.getAllTags().toArray(String[]::new);

        String tag = showTagsCombobox("message.add_tag.title", tags, true);

        if (tag == null) return;

        if (!tag.matches("^[\\w\\-]+$")) {
            mi.showErrorMessage(
                ti.translate("error.tag.invalid_string.desc"),
                ti.translate("error.general.title")
            );
            return;
        }

        List<Integer> selected = gView.getAndClearSelectedRows();

        try {
            gc.handleAddTag(selected, tag);
        } catch (IOException e) {
            mi.showErrorMessage(
                ti.translate("error.general.desc"),
                ti.translate("error.general.title"),
                e
            );
        }
    }

    /**
     * Handles the "Remove Tag" button click to remove a tag from selected images.
     * <p>
     * Prompts the user to select a tag to remove and performs the removal from the selected images.
     * </p>
     */
    private void handleRemoveTagButton() {
        if (checkIfNotSelected()) return;

        List<Integer> selected = gView.getAndClearSelectedRows();
        Set<String> tags = gc.getAllTagsInSelection(selected);

        if (tags.isEmpty()) {
            mi.showErrorMessage(
                ti.translate("error.tag.lack_of_tags.desc"),
                ti.translate("error.general.title")
            );
            return;
        }

        String tag = showTagsCombobox("message.remove_tag.title", tags.toArray(String[]::new), false);
        if (tag == null) return;

        try {
            gc.handleRemoveTag(selected, tag);
        } catch (IOException e) {
            mi.showErrorMessage(
                ti.translate("error.general.desc"),
                ti.translate("error.general.title"),
                e
            );
        }
    }

    /**
     * Handles updates to the name filter text field.
     * <p>
     * Clears the selection and applies the filter to the gallery table based on the text field's content.
     * </p>
     */
    private void handleNameFilterUpdate() {
        gView.getGalleryTable().clearSelection();
        gView.filterTable(gView.getNameFilterTextField().getText().trim());
    }

    /**
     * Checks if any images are selected in the gallery.
     * <p>
     * Displays an error message if no images are selected.
     * </p>
     *
     * @return {@code true} if no images are selected, {@code false} otherwise.
     */
    private boolean checkIfNotSelected() {
        if (!gView.ifAnySelected()) {
            mi.showErrorMessage(
                ti.translate("error.delete.no_images.desc"),
                ti.translate("error.general.title")
            );
            return true;
        }
        return false;
    }

    /**
     * Displays a combobox dialog for the user to select or input a tag.
     *
     * @param title    the translation key for the dialog title.
     * @param tags     the list of available tags.
     * @param editable determines if the combobox allows custom tag input.
     * @return the selected tag, or {@code null} if canceled.
     */
    private String showTagsCombobox(String title, String[] tags, boolean editable) {
        JComboBox<String> comboBox = new JComboBox<>();
        for (String tag : tags) {
            comboBox.addItem(ti.translate(tag));
        }
        comboBox.setEditable(editable);

        int result = mi.showConfirmationMessage(
                comboBox,
                ti.translate(title)
        );

        if (result == JOptionPane.OK_OPTION) return (String) comboBox.getSelectedItem();
        return null;
    }

    // Tasks methods.
    // Handling async tasks and long operations

    /**
     * Runs the task of adding images asynchronously.
     * <p>
     * Launches a file chooser dialog for the user to select images and then adds the chosen images
     * to the gallery. Updates the gallery UI after the task is complete.
     * </p>
     */
    private void addImagesTask() {
        gc.execute(this::prepareUiBefore, this::addImages, this::updateUiAfter)
            .exceptionally(this::handleException)
            .whenComplete(this::handleGalleryUnlock);
    }

    /**
     * Runs the task of removing selected images asynchronously.
     * <p>
     * Removes the selected images from the gallery without deleting them from the file system.
     * Updates the gallery UI after the task is complete.
     * </p>
     */
    private void removeImagesTask() {
        List<Integer> indexes = gView.getAndClearSelectedRows();
        gc.execute(this::prepareUiBefore, () -> gc.handleRemoveImages(indexes), this::updateUiAfter)
            .exceptionally(this::handleException)
            .whenComplete(this::handleGalleryUnlock);
    }

    /**
     * Runs the task of deleting selected images asynchronously.
     * <p>
     * Deletes the selected images from both the gallery and the file system.
     * Updates the gallery UI after the task is complete.
     * </p>
     */
    private void deleteImagesTask() {
        List<Integer> indexes = gView.getAndClearSelectedRows();
        gc.execute(this::prepareUiBefore, () -> gc.handleDeleteImages(indexes), this::updateUiAfter)
            .exceptionally(this::handleException)
            .whenComplete(this::handleGalleryUnlock);
    }

    /**
     * Runs the task of unifying the names of selected images asynchronously.
     * <p>
     * Applies a uniform naming convention to the selected images based on user input.
     * Updates the gallery UI after the task is complete.
     * </p>
     */
    private void unifyNamesTask() {
        gc.execute(this::prepareUiBefore, gc::handleUnifyNames, this::showMessage)
                .exceptionally(this::handleException)
                .whenComplete(this::handleGalleryUnlock);
    }

    /**
     * Runs the task of distinguishing the duplicates of selected images asynchronously.
     * <p>
     * Updates the gallery UI after the task is complete.
     * </p>
     */
    private void distinctImagesTask() {
        cc.execute(
            this::prepareUiBefore,
            this::setInputImages,
            cc::handleLoadFiles,
            cc::handleCompare,
            () -> {
                CompletableFuture<Boolean> ftr = removalConfirmation();
                ftr.thenAcceptAsync(this::reduceImages, executor)
                        .thenRun(this::updateUiAfter);
                ftr.join();
            }
        ).exceptionally(this::handleException)
        .whenComplete(this::handleGalleryUnlock);
    }

    // UI-related methods.
    // Handling UI-related operations while doing tasks.

    /**
     * Prepares the UI before executing a long operation.
     * <p>
     * This method locks the module and sets the cursor to a wait cursor to indicate that a process is ongoing.
     * </p>
     */
    private void prepareUiBefore() {
        SwingUtilities.invokeLater(() -> {
            gView.lockModule();
            umi.useCursor(CursorManagerInterface.WAIT_CURSOR);
        });
    }

    /**
     * Updates the UI after completing a long operation.
     * <p>
     * This method updates the element count label to reflect the current number of rows in the gallery table model.
     * </p>
     */
    private void updateUiAfter() {
        SwingUtilities.invokeLater(() ->
            gView.getElementCountLabel().setText(String.valueOf(gc.getRowCount()))
        );
    }

    /**
     * Handles the gallery unlocking operation after task completion.
     * <p>
     * This method unlocks the module and resets the cursor to its default state. It is called after task completion or in case of an exception.
     * </p>
     *
     * @param res The result of the completed task (ignored in this method).
     * @param ex  The exception thrown during task execution (if any).
     */
    private void handleGalleryUnlock(Void res, Throwable ex) {
        SwingUtilities.invokeLater(() -> {
            gView.unlockModule();
            umi.useCursor(CursorManagerInterface.DEFAULT_CURSOR);
        });
    }

    /**
     * Displays a confirmation dialog to the user regarding the removal of duplicates.
     * <p>
     * This method shows a confirmation message and completes the returned CompletableFuture based on the user's response.
     * </p>
     *
     * @return A CompletableFuture that will be completed thenLoad {@code true} if the user confirms, or {@code false} otherwise.
     */
    private CompletableFuture<Boolean> removalConfirmation() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        SwingUtilities.invokeLater(() -> {
            int option = mi.showConfirmationMessage(
                ti.translate("message.confirmation.duplicates_removal.desc"),
                ti.translate("message.confirmation.title")
            );

            future.complete(option == JOptionPane.OK_OPTION);
        });

        return future;
    }

    /**
     * Displays a message to the user indicating the completion of the unification task.
     * <p>
     * This method shows an informational message dialog.
     * </p>
     */
    private void showMessage() {
        SwingUtilities.invokeLater(() -> mi.showInformationMessage(
            ti.translate("message.unify_names.desc"),
            ti.translate("message.general.title")
        ));
    }

    // Operation-related methods.
    // Actual long operations while doing tasks.

    /**
     * Performs the image addition operation.
     * <p>
     * This method invokes the file chooser to add images.
     * </p>
     */
    private void addImages() {
        gView.getFileChooser().perform();
    }

    /**
     * Locks the current process, retrieves the list of selected files from the graphical view,
     * and sets them as input for further processing.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Locks the comparer process by calling {@code notifyLock()} on the comparer package.</li>
     *   <li>Retrieves a list of files based on the selected rows from the graphical view {@code gView}.</li>
     *   <li>Sets the retrieved list of files as input for further operations in the comparer controller {@code cc}.</li>
     * </ol>
     *
     * This is typically used to set up the input images for comparison after the user has selected
     * rows/files in the UI.
     */
    private void setInputImages() {
        cc.notifyLock();
        List<File> in = gc.getFiles(gView.getSelectedRows());
        cc.setInput(in);
    }

    /**
     * Reduces images based on the result of the distinct image operation.
     * <p>
     * This method either deletes or transfers files based on the provided result.
     * </p>
     *
     * @param res {@code true} if files should be deleted, {@code false} if they should be transferred.
     * @throws CompletionException If an I/O error occurs during the file operation.
     */
    private void reduceImages(boolean res) {
        try {
            List<File> out = cc.getOutput();
            gc.handleRemoveFiles(out);

            if (res) cc.handleDeleteFiles();
            else cc.handleMoveFiles();
        } catch (IOException e) {
            throw new CompletionException(e);
        } finally {
            cc.notifyUnlock();
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
