package pl.magzik.controllers;

import pl.magzik.base.interfaces.Command;
import pl.magzik.async.ExecutorServiceManager;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.comparer.processing.ComparerProcessor;
import pl.magzik.modules.comparer.file.FileHandler;
import pl.magzik.ui.localization.TranslationStrategy;
import pl.magzik.ui.cursor.CursorManagerInterface;
import pl.magzik.ui.listeners.UnifiedDocumentListener;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.GalleryView;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Manages the gallery operations including image addition, removal, deletion, and tag management.
 * <p>
 * The {@code GalleryController} interacts with the {@link GalleryView}, {@link GalleryModule}, and various
 * user interface components to perform and manage gallery operations. It uses an {@link ExecutorService} to run
 * tasks asynchronously and provides methods to handle various user actions.
 * </p>
 */
public class GalleryController {

    private final GalleryView gView;
    private final GalleryModule gModule;
    private final ComparerProcessor cp;
    private final FileHandler fh;
    private final MessageInterface mi;
    private final CursorManagerInterface umi;
    private final TranslationStrategy ti;
    private final ExecutorService executor;

    /**
     * Constructs a {@code GalleryController} with the specified view, module, and interfaces.
     * <p>
     * Initializes the UI components, adds listeners to the UI elements, and sets up the executor service.
     * </p>
     *
     * @param gView the {@link GalleryView} used to interact with the UI.
     * @param gModule the {@link GalleryModule} that handles gallery data and operations.
     * @param umi the {@link CursorManagerInterface} for managing the user interface state.
     * @param mi the {@link MessageInterface} for displaying messages to the user.
     * @param ti the {@link TranslationStrategy} for translating messages.
     */
    public GalleryController(GalleryView gView, GalleryModule gModule, ComparerProcessor cp, FileHandler fh, CursorManagerInterface umi, MessageInterface mi, TranslationStrategy ti) {
        this.ti = ti;
        this.umi = umi;
        this.mi = mi;
        this.gModule = gModule;
        this.cp = cp;
        this.fh = fh;
        this.gView = gView;
        this.executor = ExecutorServiceManager.getInstance().getExecutorService();

        // Initialize

        initialize();

        // Listeners

        addListeners();
    }

    /**
     * Initializes the gallery view with the necessary parts and settings.
     * <p>
     * Sets the file chooser, table model, row sorter, and updates the element count label.
     * </p>
     */
    private void initialize() {
        gView.setFileChooser(this);

        gView.getGalleryTable().setModel(gModule.getGalleryTableModel());
        gView.getGalleryTable().setRowSorter(gModule.getTableRowSorter());

        gView.getElementCountLabel().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount()));
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
        gModule.getGalleryTableModel().addTableModelListener(this::handleGalleryTableUpdate);

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

        int[] selected = getGalleryTableSelectedRows();

        try {
            for (int idx : selected) {
                gModule.openImage(idx);
            }
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

        String[] tags = gModule.getExistingTags().toArray(String[]::new);

        String tag = showTagsCombobox("message.add_tag.title", tags, true);

        if (tag == null || !tag.matches("^[\\w\\-]+$")) {
            mi.showErrorMessage(
                ti.translate("error.tag.invalid_string.desc"),
                ti.translate("error.general.title")
            );
            return;
        }

        int[] selected = getGalleryTableSelectedRows();

        doAndSave(() -> {
            try {
                for (int idx : selected) {
                    gModule.addTag(idx, tag);
                }
            } catch (IOException e) {
                mi.showErrorMessage(
                    ti.translate("error.general.desc"),
                    ti.translate("error.general.title"),
                    e
                );
            }
        });
    }

    /**
     * Handles the removal of tags from images when the "Remove Tag" button is pressed.
     * <p>
     * Displays a tag combobox for user input and removes the selected tag from the selected images.
     * </p>
     */
    private void handleRemoveTagButton() {
        if (checkIfNotSelected()) return;

        int[] selected = getGalleryTableSelectedRows();

        String[] tags = Arrays.stream(selected)
                .mapToObj(gModule::getTags)
                .flatMap(Arrays::stream)
                .distinct()
                .toArray(String[]::new);

        if (tags.length == 0) {
            mi.showErrorMessage(
                ti.translate("error.tag.lack_of_tags.desc"),
                ti.translate("error.general.title")
            );

            return;
        }

        String tag = showTagsCombobox("message.remove_tag.title", tags, false);
        if (tag == null) return;

        doAndSave(() -> {
            for (int idx : selected) gModule.removeTag(idx, tag);
        });
    }

    /**
     * Handles updates to the gallery table model.
     * <p>
     * Saves the current state to a file if the first column is updated.
     * </p>
     *
     * @param e the {@link TableModelEvent} representing the update event.
     */
    private void handleGalleryTableUpdate(TableModelEvent e) {
        int c = e.getColumn();

        if (c == 0 && e.getType() == TableModelEvent.UPDATE) {
            try {
                gModule.saveToFile();
            } catch (IOException ex) {
                mi.showErrorMessage(
                    ti.translate("error.save.ioexception.desc"),
                    ti.translate("error.general.title")
                );
            }
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
        gModule.filterTable(gView.getNameFilterTextField().getText().trim());
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
        if (gView.getGalleryTable().getSelectedRows().length == 0) {
            mi.showErrorMessage(
                ti.translate("error.delete.no_images.desc"),
                ti.translate("error.general.title")
            );
            return true;
        }
        return false;
    }

    /**
     * Gets the indices of the selected rows in the gallery table.
     *
     * @return an array of selected row indices.
     */
    private int[] getGalleryTableSelectedRows() {
        int[] rows = gView.getGalleryTable().getSelectedRows();
        gView.getGalleryTable().clearSelection();

        return rows;
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

    /**
     * Executes the provided {@link Command} and then attempts to save the current state to a file.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Executes the provided {@link Command}.</li>
     *     <li>Attempts to save the current state using {@link GalleryModule#saveToFile()}.</li>
     * </ul>
     * If an {@link IOException} occurs during the save operation, an error message is displayed to the user
     * using the {@link MessageInterface} instance.
     * </p>
     *
     * @param action the {@link Command} to be executed. Must not be {@code null}.
     * @throws NullPointerException if the provided {@code action} is {@code null}.
     */
    private void doAndSave(Command action) {
        Objects.requireNonNull(action);
        action.execute();

        try {
            gModule.saveToFile();
        } catch (IOException e) {
            mi.showErrorMessage(
                ti.translate("error.general.desc"),
                ti.translate("error.general.title"),
                e
            );
        }
    }

    /**
     * Executes the provided {@link Command} and then attempts to save the current state to a file asynchronously.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Executes the provided {@link Command}.</li>
     *     <li>Attempts to save the current state using {@link GalleryModule#saveToFile()}.</li>
     * </ul>
     * If an {@link IOException} occurs during the save operation, it is wrapped in an instance of the specified
     * {@link RuntimeException} subclass and thrown.
     * </p>
     * <p>
     * Note: This method does not handle UI updates or error messages directly. It is designed to be used in an
     * asynchronous context where exceptions will be handled by the caller.
     * </p>
     *
     * @param action the {@link Command} to be executed. Must not be {@code null}.
     * @param excClass the class of {@link RuntimeException} to wrap the {@link IOException} into. This class must
     *                 have a constructor that accepts a {@link String} and a {@link Throwable} as parameters.
     * @throws NullPointerException if the provided {@code action} is {@code null}.
     * @throws IllegalArgumentException if the provided {@code excClass} is not a subclass of {@link RuntimeException}
     *                                  or does not have the required constructor.
     * @throws RuntimeException if instantiation of the specified {@code excClass} fails or if the {@link IOException}
     *                          cannot be wrapped properly.
     */
    private void doAndSaveAsync(Command action, Class<? extends RuntimeException> excClass) {
        Objects.requireNonNull(action);
        action.execute();

        try {
            gModule.saveToFile();
        } catch (IOException e) {
            throw createWrappedException(excClass, e);
        }
    }

    /**
     * Creates a new instance of the specified RuntimeException class, with the given cause.
     * <p>
     * This method uses reflection to instantiate the exception class and sets the cause to the provided exception.
     * </p>
     *
     * @param excClass the class of the RuntimeException to create
     * @param cause the cause of the exception
     * @return a new instance of the specified RuntimeException with the cause set
     * @throws RuntimeException if instantiation fails or the class is not a subclass of RuntimeException
     */
    private RuntimeException createWrappedException(Class<? extends RuntimeException> excClass, Throwable cause) {
        Objects.requireNonNull(excClass);
        Objects.requireNonNull(cause);

        try {
            if (!RuntimeException.class.isAssignableFrom(excClass)) {
                throw new IllegalArgumentException("Provided class is not a RuntimeException subclass.");
            }

            return excClass
                .getConstructor(Throwable.class)
                .newInstance(cause);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Couldn't create an instance of the exception", ex);
        }
    }

    /**
     * Handles the addition of images from a given list of file paths.
     * <p>
     * This method is designed to be used externally, allowing for the addition of images
     * to the gallery using a list of file paths. It interacts with the {@link GalleryModule}
     * to perform the actual addition. If an {@link IOException} occurs during the process,
     * an error message is displayed to the user.
     * </p>
     *
     * @param list a {@link List} of {@link String} representing the file paths of the images
     *             to be added to the gallery.
     */
    public void handleAddImages(List<String> list) {
        // To be used externally.
        try {
            gModule.addImage(list);
        } catch (IOException e) {
            mi.showErrorMessage(
                ti.translate("error.add.ioexception.desc"),
                ti.translate("error.general.title")
            );
        }
    }

    // Tasks methods.
    // Handling async tasks and long operations

    /**
     * Initiates an asynchronous task to add images.
     * <p>
     * The task involves preparing the UI, adding images, and then updating the UI.
     * If an exception occurs during the execution, it will be handled and the gallery unlock operation will be completed.
     * </p>
     */
    private void addImagesTask() {
        executeAsync(this::addImages);
    }

    /**
     * Initiates an asynchronous task to remove images.
     * <p>
     * The task involves preparing the UI, performing the removal operation, and then updating the UI.
     * If an exception occurs during the execution, it will be handled and the gallery unlock operation will be completed.
     * </p>
     */
    private void removeImagesTask() {
        executeAsync(() -> performRemovalTask(gModule::removeImage));
    }

    /**
     * Initiates an asynchronous task to deleteFiles images.
     * <p>
     * The task involves preparing the UI, performing the deletion operation, and then updating the UI.
     * If an exception occurs during the execution, it will be handled and the gallery unlock operation will be completed.
     * </p>
     */
    private void deleteImagesTask() {
        executeAsync(() -> performRemovalTask(gModule::deleteImage));
    }

    /**
     * Initiates an asynchronous task to handle image distinct operations.
     * <p>
     * The task involves preparing the UI, performing distinct image operations, updating the UI, confirming removal, and then reducing images.
     * If an exception occurs during the execution, it will be handled and the gallery unlock operation will be completed.
     * </p>
     */
    private void distinctImagesTask() {
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
     * If an exception occurs during the execution, it will be handled and the gallery unlock operation will be completed.
     * </p>
     */
    private void unifyNamesTask() {
        executeAsync(this::unifyNames, this::save, this::showMessage);
    }


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
        SwingUtilities.invokeLater(() -> gView.getElementCountLabel().setText(String.valueOf(gModule.getGalleryTableModel().getRowCount())));
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
     * @return A CompletableFuture that will be completed with {@code true} if the user confirms, or {@code false} otherwise.
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
     * Performs a removal task using the provided consumer.
     * <p>
     * This method collects the selected image IDs, clears the selection, and then applies the removal task.
     * It also saves the changes to a file.
     * </p>
     *
     * @param consumer A Consumer that takes a list of image IDs to perform the removal operation.
     * */
    private void performRemovalTask(Consumer<List<Integer>> consumer) {
        List<Integer> selected = gView.getAndClearSelectedRows();

        if (selected.isEmpty()) {
            throw new CompletionException(new NullPointerException("No images selected."));
        }

        doAndSaveAsync(() -> consumer.accept(selected), CompletionException.class);
    }

    /**
     * Performs the distinct image operations.
     * <p>
     * This method prepares the comparer with selected image IDs and then performs the comparison and extraction operations.
     * </p>
     *
     * @throws CompletionException If an I/O error occurs during the preparation or comparison, or If the thread is interrupted during the operation, or If the operation times out.
     */
    private void distinctImages() {
        try {
            cp.lock();
            List<File> in = gModule.getFiles(gView.getGalleryTable().getSelectedRows());
            in = fh.loadFiles(in);
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
        try {
            List<File> out = cp.getOutput();
            cp.release();

            gModule.performReduction(out);

            if (res) fh.moveFiles(out);
            else fh.deleteFiles(out);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Unifies image names.
     * <p>
     * This method performs the operation to unify image names.
     * </p>
     *
     * @throws CompletionException If an I/O error occurs during the unification process.
     */
    private void unifyNames() {
        try {
            gModule.unifyNames();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Saves the current state to a file.
     * <p>
     * This method performs the save operation for the current state.
     * </p>
     *
     * @throws CompletionException If an I/O error occurs during the save operation.
     */
    private void save() {
        try {
            gModule.saveToFile();
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
