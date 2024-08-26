package pl.magzik.ui.components.general;

import java.io.File;

/**
 * Interface for a file chooser component that allows for selecting files or directories.
 * Provides methods to reset the file chooser to a default state and open the file chooser dialog.
 *
 * <p>The interface includes a default method {@link #perform()} which resets the file chooser
 * and then opens it, returning whether the operation was successful.</p>
 */
public interface FileChooserInterface {
    /**
     * Default path used when resetting the file chooser.
     */
    File DEFAULT_PATH = new File(System.getProperty("user.home"));

    /**
     * Resets the file chooser to its default state.
     * Typically, sets the current directory to {@link #DEFAULT_PATH}.
     */
    void reset();

    /**
     * Opens the file chooser dialog.
     *
     * @return {@code true} if the user selected a file or directory, {@code false} otherwise.
     */
    boolean open();

    /**
     * Resets the file chooser and then opens the file chooser dialog.
     *
     * @return {@code true} if the user selected a file or directory, {@code false} otherwise.
     */
    default boolean perform() {
        reset();
        return open();
    }
}
