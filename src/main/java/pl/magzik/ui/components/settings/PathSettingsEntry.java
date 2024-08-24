package pl.magzik.ui.components.settings;

import pl.magzik.ui.components.Utility;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;

/**
 * Implementation of {@link SettingsEntry} for paths ({@link JTextField} and {@link JButton})
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
    private final JFileChooser fileChooser;

    /**
     * @param label {@link String} title of a label component.
     * @param value {@link JPanel} panel to be used as a value component
     *                           (Panel must contain only {@link JTextField} and {@link JButton}).
     * @throws NullPointerException if given panel doesn't contain {@link JTextField} or {@link JButton}.
     * */
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
        pathTextField.setFont(Utility.fontHelveticaPlain);
        pathTextField.setBorder(textFieldBorder);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("view.settings.file_chooser.destination.title");
        fileChooser.setApproveButtonText("view.settings.file_chooser.destination.button.approve");
        resetFileChooser();

        openButton.addActionListener(_ -> openFileChooser());
    }

    @Override
    public String getValue() {
        return pathTextField.getText();
    }

    @Override
    protected void setValueProperty(String value) {
        pathTextField.setText(value);
    }

    /**
     * Returns {@link JFileChooser}. Used only for component translation.
     * @return {@link JFileChooser}
     * */
    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    /**
     * Resets {@link JFileChooser}'s a path to {@code System.getProperty("user.home")}
     * */
    private void resetFileChooser() {
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    }

    /**
     * Opens {@link JFileChooser} gui-wise and sets, if selected, directory to a {@link JTextField}
     * */
    private void openFileChooser() {
        resetFileChooser();
        int result = fileChooser.showOpenDialog(openButton);
        if (result == JFileChooser.APPROVE_OPTION) {
            setValue(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
}
