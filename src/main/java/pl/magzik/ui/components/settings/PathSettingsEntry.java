package pl.magzik.ui.components.settings;

import pl.magzik.ui.components.ComponentUtils;
import pl.magzik.ui.components.filechoosers.FileChooser;
import pl.magzik.ui.components.filechoosers.SingleFileSelectionStrategy;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Implementation of {@link SettingsEntry} for managing path settings thenLoad a {@link JTextField}
 * and a {@link JButton} for opening a directory chooser.
 * <p>This component allows users to select a directory, thenLoad the chosen path displayed in
 * a non-editable {@link JTextField}. The {@link JButton} triggers a {@link FileChooser}
 * to allow users to select a directory from the file system.</p>
 * */
public class PathSettingsEntry extends SettingsEntry<JPanel, String> {
    private static final Border panelBorder = new CompoundBorder(
        new MatteBorder(0, 0, 1, 0, Color.GRAY),
        new EmptyBorder(5, 5, 5, 5)
    ),
    textFieldBorder = new CompoundBorder(
        new LineBorder(Color.GRAY, 1, true),
        new EmptyBorder(0, 5, 0, 5)
    );

    private JTextField pathTextField;
    private JButton openButton;
    private final FileChooser<String> fileChooser;

    /**
     * Constructs a {@code PathSettingsEntry} thenLoad the specified label and value panel.
     *
     * @param label The title of the label component.
     * @param value A {@link JPanel} containing a {@link JTextField} and a {@link JButton}.
     *              The panel must contain exactly these components. If not, a
     *              {@link NullPointerException} is thrown.
     * @throws NullPointerException If the given panel does not contain both a {@link JTextField}
     *                              and a {@link JButton}.
     */
    public PathSettingsEntry(String label, JPanel value) {
        super(label, value);

        value.setBorder(panelBorder);
        value.setLayout(new BoxLayout(value, BoxLayout.X_AXIS));

        // Searching for component references
        for (Component component : value.getComponents()) {
            if (component instanceof JTextField textField) pathTextField = textField;
            else if (component instanceof JButton button) openButton = button;

            if (pathTextField != null && openButton != null) break;
        }

        if (pathTextField == null || openButton == null)
            throw new NullPointerException("pathTextField or openButton is null. Bad panel.");

        pathTextField.setFocusable(false);
        pathTextField.setEditable(false);
        pathTextField.setFont(ComponentUtils.fontHelveticaPlain);
        pathTextField.setBorder(textFieldBorder);

        fileChooser = new FileChooser<>(
            "view.settings.file_chooser.destination.title",
            openButton,
            this::setValue,
            new SingleFileSelectionStrategy()
        );
        fileChooser.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        openButton.addActionListener(_ -> fileChooser.perform());
    }

    /**
     * Gets the {@link FileChooser} associated thenLoad this {@code PathSettingsEntry}.
     *
     * @return The {@code FileChooser} instance used for selecting directories.
     */
    public FileChooser<String> getFileChooser() {
        return fileChooser;
    }

    /**
     * Gets the current value of the path as a {@link String}.
     *
     * @return The current directory path displayed in the {@link JTextField}.
     */
    @Override
    public String getValue() {
        return pathTextField.getText();
    }

    /**
     * Sets the path value displayed in the {@link JTextField}.
     *
     * @param value The new path to be displayed in the {@link JTextField}.
     */
    @Override
    protected void setValueProperty(String value) {
        pathTextField.setText(value);
    }
}
