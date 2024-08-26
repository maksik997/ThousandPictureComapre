package pl.magzik.ui.components.general;

import java.io.File;

/**
 * Interface for a file chooser component that allows for selecting files or directories.
 * Provides methods to reset the file chooser to a default state and open the file chooser dialog.
 *
 * <p>This interface is intended to be implemented by classes that provide specific
 * file selection mechanisms, such as single or multiple file selection strategies.</p>
 *
 * <p>The interface includes a default method {@link #perform()} which resets the file chooser
 * and then opens it, returning whether the operation was successful.</p>
 */
public interface FileChooserInterface {
    /**
     * The default path used when resetting the file chooser.
     * This is typically set to the user's home directory.
     */
    File DEFAULT_PATH = new File(System.getProperty("user.home"));

    /**
     * Resets the file chooser to its default state.
     * Typically, this method sets the current directory of the file chooser
     * to the {@link #DEFAULT_PATH}.
     */
    void reset();

    /**
     * Opens the file chooser dialog, allowing the user to select files or directories.
     *
     * @return {@code true} if the user successfully selected a file or directory,
     *         {@code false} if the user canceled the operation or if no file was selected.
     */
    boolean open();

    /**
     * Resets the file chooser and then opens the file chooser dialog.
     * This is a convenience method that combines the actions of {@link #reset()}
     * and {@link #open()}.
     *
     * @return {@code true} if the user successfully selected a file or directory,
     *         {@code false} if the user canceled the operation or if no file was selected.
     */
    default boolean perform() {
        reset();
        return open();
    }
}
