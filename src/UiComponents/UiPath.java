package UiComponents;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UiPath extends JPanel {

    // todo
    //  Resize ability for pathTextField,

    private String path;
    private final JLabel pathLabel;
    private final JTextField pathTextField;
    private final JButton pathButton;

    private final JFileChooser fileChooser;

    public UiPath() {
        this.pathLabel = new JLabel("Path: ", SwingConstants.RIGHT);
        this.pathTextField = new JTextField(49);
        this.pathButton = Utility.buttonFactory("Open", new Insets(10, 15, 10, 15));
        this.fileChooser = new JFileChooser();

        this.pathLabel.setFont(Utility.fontHelveticaPlain);
        this.pathTextField.setEditable(false);
        this.pathTextField.setFocusable(false);
        this.pathTextField.setBorder(
            new CompoundBorder(
                this.pathTextField.getBorder(),
                new EmptyBorder(5, 0, 5, 0)
            )
        );
        this.pathTextField.setFont(Utility.fontHelveticaPlain);

        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.add(this.pathLabel);
        this.add(this.pathTextField);
        this.add(this.pathButton);
    }

    public JLabel getPathLabel() {
        return pathLabel;
    }

    public JTextField getPathTextField() {
        return pathTextField;
    }

    public JButton getPathButton() {
        return pathButton;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public String getPath() {
        return path;
    }

    private void updatePath(String str) {
        this.pathTextField.setText(str);
        this.path = str.isEmpty() ? null : str;
    }

    public boolean openFileChooser() {
        // True means that user has picked a path.
        // False means otherwise
        int file = fileChooser.showOpenDialog(this);
        if(file == JFileChooser.APPROVE_OPTION) {
            updatePath(fileChooser.getSelectedFile().getAbsolutePath());
            return true;
        }
        return false;
    }

    public void clear() {
        updatePath("");
    }
}
