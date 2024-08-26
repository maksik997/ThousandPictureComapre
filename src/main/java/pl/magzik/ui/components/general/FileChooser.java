package pl.magzik.ui.components.general;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Implementation of {@link FileChooserInterface} using {@link JFileChooser}.
 * Provides functionality to open a file chooser dialog for selecting files.
 *
 * <p>This class initializes the file chooser with a given dialog title and a button for opening the dialog.
 * It also accepts a {@link Consumer} to handle the selected file path.</p>
 */
public class FileChooser implements FileChooserInterface {
    protected final JFileChooser fileChooser;
    private final JButton openButton;
    private final Consumer<String> consumer;

    /**
     * Constructs a {@code FileChooser} with the specified dialog title, open button, and file path consumer.
     *
     * @param dialogTitle The title of the file chooser dialog.
     * @param openButton The button that triggers the file chooser dialog.
     * @param consumer A {@code Consumer} to handle the selected file path.
     */
    public FileChooser(String dialogTitle, JButton openButton, Consumer<String> consumer) {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setApproveButtonText("view.settings.file_chooser.destination.button.approve");

        this.openButton = openButton;
        this.consumer = consumer;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    @Override
    public void reset() {
        fileChooser.setCurrentDirectory(DEFAULT_PATH);
    }

    @Override
    public boolean open() {
        int result = fileChooser.showOpenDialog(openButton);
        if (result == JFileChooser.APPROVE_OPTION) {
            consumer.accept(fileChooser.getSelectedFile().getAbsolutePath());
            return true;
        }
        return false;
    }
}
