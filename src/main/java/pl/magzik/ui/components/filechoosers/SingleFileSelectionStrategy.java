package pl.magzik.ui.components.filechoosers;

import javax.swing.*;

/**
 * Implementation of {@link FileSelectionStrategy} for selecting a single file.
 * <p>
 * This strategy configures the {@link JFileChooser} to allow only a single file to be selected
 * and processes the selection by returning the absolute path of the selected file.
 * </p>
 */
public class SingleFileSelectionStrategy implements FileSelectionStrategy<String> {

    /**
     * Configures the {@link JFileChooser} to allow only single file selection.
     *
     * @param fileChooser The {@link JFileChooser} to configure.
     */
    @Override
    public void configure(JFileChooser fileChooser) {
        fileChooser.setMultiSelectionEnabled(false);
    }

    /**
     * Processes the selection by retrieving the absolute path of the selected file.
     *
     * @param fileChooser The {@link JFileChooser} from which the selection is retrieved.
     * @return The absolute path of the selected file.
     */
    @Override
    public String processSelection(JFileChooser fileChooser) {
        return fileChooser.getSelectedFile().getAbsolutePath();
    }
}
