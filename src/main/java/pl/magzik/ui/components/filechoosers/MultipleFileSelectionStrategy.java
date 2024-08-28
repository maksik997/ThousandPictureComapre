package pl.magzik.ui.components.filechoosers;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.io.File;

/**
 * Implementation of {@link FileSelectionStrategy} for selecting multiple files.
 * <p>
 * This strategy configures the {@link JFileChooser} to allow multiple files to be selected
 * and processes the selection by returning a list of the absolute paths of the selected files.
 * </p>
 */
public class MultipleFileSelectionStrategy implements FileSelectionStrategy<List<String>> {

    /**
     * Configures the {@link JFileChooser} to allow multiple file selections.
     *
     * @param fileChooser The {@link JFileChooser} to configure.
     */
    @Override
    public void configure(JFileChooser fileChooser) {
        fileChooser.setMultiSelectionEnabled(true);
    }

    /**
     * Processes the selection by retrieving the absolute paths of the selected files.
     *
     * @param fileChooser The {@link JFileChooser} from which the selection is retrieved.
     * @return A list of absolute paths of the selected files.
     */
    @Override
    public List<String> processSelection(JFileChooser fileChooser) {
        return Arrays.stream(fileChooser.getSelectedFiles()).map(File::toString).toList();
    }
}
