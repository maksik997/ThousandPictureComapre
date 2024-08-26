package pl.magzik.ui.components.general;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * A specialized {@link FileChooser} that only allows selection of directories.
 *
 * <p>This class extends {@link FileChooser} and configures the {@link JFileChooser} to
 * only allow directory selection.</p>
 */
public class DirectoryFileChooser extends FileChooser {

    /**
     * Constructs a {@code DirectoryFileChooser} with the specified dialog title, open button, and directory path consumer.
     *
     * @param dialogTitle The title of the file chooser dialog.
     * @param openButton The button that triggers the file chooser dialog.
     * @param consumer A {@code Consumer} to handle the selected directory path.
     */
    public DirectoryFileChooser(String dialogTitle, JButton openButton, Consumer<String> consumer) {
        super(dialogTitle, openButton, consumer);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
}
