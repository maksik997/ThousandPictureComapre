package pl.magzik.ui.components.filechoosers;

import javax.swing.*;

/**
 * Strategy interface for configuring and processing file selection in a {@link JFileChooser}.
 * <p>
 * Implementations of this interface provide specific configurations for the {@link JFileChooser}
 * (e.g., single or multiple file selection) and process the selected files accordingly.
 * </p>
 *
 * @param <T> The type of the result returned by the {@link #processSelection(JFileChooser)} method.
 */
public interface FileSelectionStrategy<T> {

    /**
     * Configures the provided {@link JFileChooser} based on the specific strategy.
     * <p>
     * For example, a strategy may enable or disable multi-selection, set filters, or adjust other settings.
     * </p>
     *
     * @param fileChooser The {@link JFileChooser} to configure.
     */
    void configure(JFileChooser fileChooser);

    /**
     * Processes the user's selection in the {@link JFileChooser} and returns the result.
     *
     * @param fileChooser The {@link JFileChooser} from which the selection is retrieved.
     * @return The result of the selection processing, which could be a single file path, a list of file paths, etc.
     */
    T processSelection(JFileChooser fileChooser);
}
