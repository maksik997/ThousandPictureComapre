package pl.magzik.ui.components.general;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * A generic file chooser component that supports custom file selection strategies.
 * <p>
 * This class encapsulates a {@link JFileChooser} and allows for configuration and processing
 * of file selections based on a provided {@link FileSelectionStrategy}.
 * </p>
 *
 * @param <T> The type of the result returned by the file selection strategy.
 */
public class FileChooser <T> implements FileChooserInterface {
    private final JFileChooser fileChooser;
    private final JButton openButton;
    private final Consumer<T> consumer;
    private final FileSelectionStrategy<T> strategy;

    /**
     * Constructs a {@code FileChooser} with the specified dialog title,
     * open button, consumer, and file selection strategy.
     *
     * @param dialogTitle The title of the file chooser dialog.
     * @param openButton The button that triggers the file chooser dialog.
     * @param consumer A {@code Consumer} to handle the result of the file selection.
     * @param fileSelectionStrategy The strategy used to configure and process the file selection.
     */
    public FileChooser(String dialogTitle, JButton openButton, Consumer<T> consumer, FileSelectionStrategy<T> fileSelectionStrategy) {
        this.fileChooser = new JFileChooser();
        this.openButton = openButton;
        this.consumer = consumer;
        this.strategy = fileSelectionStrategy;

        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setApproveButtonText("view.settings.file_chooser.destination.button.approve");
        strategy.configure(fileChooser);
    }

    /**
     * Returns the underlying {@link JFileChooser} instance.
     *
     * @return The {@link JFileChooser} instance used by this component.
     */
    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * Resets the file chooser to its default state, typically setting the current directory
     * to the user's home directory.
     */
    @Override
    public void reset() {
        fileChooser.setCurrentDirectory(DEFAULT_PATH);
    }

    /**
     * Opens the file chooser dialog, allowing the user to select files or directories.
     * <p>
     * If the user approves the selection, the selected files or directories are processed
     * using the provided {@link FileSelectionStrategy}, and the result is passed to the consumer.
     * </p>
     *
     * @return {@code true} if the user successfully selected a file or directory,
     *         {@code false} if the user canceled the operation or if no file was selected.
     */
    @Override
    public boolean open() {
        int result = fileChooser.showOpenDialog(openButton);
        if (result == JFileChooser.APPROVE_OPTION) {
            consumer.accept(strategy.processSelection(fileChooser));
            return true;
        }
        return false;
    }
}
