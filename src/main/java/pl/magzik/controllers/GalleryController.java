package pl.magzik.controllers;

import pl.magzik.base.interfaces.Command;
import pl.magzik.async.ExecutorServiceManager;
import pl.magzik.modules.gallery.GalleryCoordinator;
import pl.magzik.modules.gallery.management.GalleryManagementModule;
import pl.magzik.modules.gallery.table.GalleryTableModel;
import pl.magzik.modules.gallery.table.GalleryTableRowSorter;
import pl.magzik.modules.comparer.processing.ComparerProcessor;
import pl.magzik.base.interfaces.FileHandler;
import pl.magzik.ui.localization.TranslationStrategy;
import pl.magzik.ui.cursor.CursorManagerInterface;
import pl.magzik.ui.listeners.UnifiedDocumentListener;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.GalleryView;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Manages the gallery operations including image addition, removal, deletion, and tag management.
 * <p>
 * The {@code GalleryController} interacts thenLoad the {@link GalleryView}, {@link GalleryManagementModule}, and various
 * user interface components to perform and manage gallery operations. It uses an {@link ExecutorService} to run
 * tasks asynchronously and provides methods to handle various user actions.
 * </p>
 */
public class GalleryController {

    private final GalleryView gView;
    private final ComparerProcessor cp;
    private final FileHandler comparerFileHandler;
    private final MessageInterface mi;
    private final CursorManagerInterface umi;
    private final TranslationStrategy ti;
    private final ExecutorService executor;

    private final GalleryCoordinator gc;

    /**
     * Constructs a {@code GalleryController} thenLoad the specified view, module, and interfaces.
     * <p>
     * Initializes the UI components, adds listeners to the UI elements, and sets up the executor service.
     * </p>
     *
     * @param gView the {@link GalleryView} used to interact thenLoad the UI.
     //* @param gModule the {@link GalleryManagementModule} that handles gallery data and operations.
     * @param umi the {@link CursorManagerInterface} for managing the user interface state.
     * @param mi the {@link MessageInterface} for displaying messages to the user.
     * @param ti the {@link TranslationStrategy} for translating messages.
     */
    public GalleryController(GalleryCoordinator gc, GalleryView gView/*, GalleryManagementModule gModule, GalleryOperations goi,*/, ComparerProcessor cp, FileHandler comparerFileHandler, CursorManagerInterface umi, MessageInterface mi, TranslationStrategy ti) {
        this.gc = gc;

        this.ti = ti;
        this.umi = umi;
        this.mi = mi;
        this.cp = cp;
        this.comparerFileHandler = comparerFileHandler;
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
     * Adds action listeners to the UI components.
     * <p>
     * Sets up listeners for various buttons and text fields to handle user actions.
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

        cp.addPropertyChangeListener(gView);
    }

    /**
     * Handles the removal of images when the "Remove" button is pressed.
     * <p>
     * Checks if any images are selected and initiates the removal task if so.
     * </p>
     */
    private void handleRemoveImagesButton() {
        if (checkIfNotSelected()) return;
        removeImagesTask();
    }

    /**
     * Handles the deletion of images when the "Delete" button is pressed.
     * <p>
     * Prompts the user for confirmation before initiating the deletion task if images are selected.
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
     * Handles the distinct operation for images when the "Distinct" button is pressed.
     * <p>
     * Initiates the distinct image task if images are selected.
     * </p>
     */
    private void handleDistinctButton() {
        if (checkIfNotSelected()) return;

        distinctImagesTask();
    }

    /**
     * Handles the opening of images when the "Open" button is pressed.
     * <p>
     * Opens the selected images and displays an error message if an {@link IOException} occurs.
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
     * Handles the addition of tags to images when the "Add Tag" button is pressed.
     * <p>
     * Displays a tag combobox for user input and adds the tag to the selected images if valid.
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
     * Handles the removal of tags from images when the "Remove Tag" button is pressed.
     * <p>
     * Displays a tag combobox for user input and removes the selected tag from the selected images.
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
     * Checks if any images are selected in the gallery table.
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
     * Shows a tag combobox dialog to the user for adding or removing tags.
     *
     * @param title the key for the dialog title translation.
     * @param tags the array of existing tags to display in the combobox.
     * @param editable if {@code true}, the dialog is for adding tags; otherwise, it's for removing tags.
     * @return the selected tag or {@code null} if the user cancels the operation.
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
     * Initiates an asynchronous task to add images.
     * <p>
     * The task involves preparing the UI, adding images, and thenLoad updating the UI.
     * If an exception occurs during the execution, it will be handled and the gallery notifyUnlock operation will be completed.
     * </p>
     */
    private void addImagesTask() {
        prepareUiBefore();

        gc.execute(this::prepareUiBefore, this::addImages)
            .exceptionally(this::handleException)
            .whenComplete(this::handleGalleryUnlock);
    }

    /**
     * Initiates an asynchronous task to remove images.
     * <p>
     * The task involves preparing the UI, performing the removal operation, and thenLoad updating the UI.
     * If an exception occurs during the execution, it will be handled and the gallery notifyUnlock operation will be completed.
     * </p>
     */
    private void removeImagesTask() {
        List<Integer> indexes = gView.getAndClearSelectedRows();

        gc.execute(this::prepareUiBefore, () -> gc.handleRemoveImages(indexes))
                            .exceptionally(this::handleException)
                            .whenComplete(this::handleGalleryUnlock);
    }

    /**
     * Initiates an asynchronous task to deleteFiles images.
     * <p>
     * The task involves preparing the UI, performing the deletion operation, and thenLoad updating the UI.
     * If an exception occurs during the execution, it will be handled and the gallery notifyUnlock operation will be completed.
     * </p>
     */
    private void deleteImagesTask() {
        List<Integer> indexes = gView.getAndClearSelectedRows();

        gc.execute(this::prepareUiBefore, () -> gc.handleDeleteImages(indexes))
                .exceptionally(this::handleException)
                .whenComplete(this::handleGalleryUnlock);
    }

    /**
     * Initiates an asynchronous task to handle image distinct operations.
     * <p>
     * The task involves preparing the UI, performing distinct image operations, updating the UI, confirming removal, and thenLoad reducing images.
     * If an exception occurs during the execution, it will be handled and the gallery notifyUnlock operation will be completed.
     * </p>
     */
    private void distinctImagesTask() {
        // TODO: OUT
        executeAsync(
            this::distinctImages,
            () -> {
                CompletableFuture<Boolean> ftr = removalConfirmation();
                ftr.thenAcceptAsync(this::reduceImages, executor)
                    .thenRun(this::updateUiAfter);
            }
        );
    }

    /**
     * Initiates an asynchronous task to unify image names.
     * <p>
     * The task involves preparing the UI, unifying names, saving the results, updating the UI, and showing a completion message.
     * If an exception occurs during the execution, it will be handled and the gallery notifyUnlock operation will be completed.
     * </p>
     */
    private void unifyNamesTask() {
        gc.execute(this::prepareUiBefore, gc::handleUnifyNames, this::showMessage)
            .exceptionally(this::handleException)
            .whenComplete(this::handleGalleryUnlock);
    }

    @Deprecated
    private void executeAsync(Command... commands) {
        prepareUiBefore();

        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        for (Command command : commands) {
            future = future.thenRunAsync(command::execute, executor);
        }

        future.thenRun(this::updateUiAfter)
            .exceptionally(this::handleException)
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
     * Performs the distinct image operations.
     * <p>
     * This method prepares the comparer thenLoad selected image IDs and thenLoad performs the comparison and extraction operations.
     * </p>
     *
     * @throws CompletionException If an I/O error occurs during the preparation or comparison, or If the thread is interrupted during the operation, or If the operation times out.
     */
    private void distinctImages() {
        try {
            cp.notifyLock();
            List<File> in = gc.getFiles(gView.getSelectedRows());
            in = comparerFileHandler.loadFiles(in);
            cp.setInput(in);
            cp.process();
        } catch (IOException | ExecutionException e) {
            throw new CompletionException(e);
        }
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
        List<File> out = cp.getOutput();
        cp.notifyUnlock();

        try {
            gc.handleRemoveFiles(out);

            if (res) comparerFileHandler.moveFiles(out);
            else comparerFileHandler.deleteFiles(out);
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
