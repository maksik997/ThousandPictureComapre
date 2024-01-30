package UiComponents;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UiPath extends JPanel {
    private String path;
    private final JTextField pathTextField_;
    private final JButton pathButton;
    private final JFileChooser fileChooser_;

    public UiPath() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel pathLabel = new JLabel("Path: ", SwingConstants.RIGHT);
        this.pathTextField_ = new JTextField();
        this.pathButton = Utility.buttonFactory("Open", new Insets(10, 15, 10, 15));
        this.fileChooser_ = new JFileChooser();

        pathLabel.setFont(Utility.fontHelveticaBold);
        this.pathTextField_.setEditable(false);
        this.pathTextField_.setFocusable(false);
        this.pathTextField_.setBorder(
            new CompoundBorder(
                this.pathTextField_.getBorder(),
                new EmptyBorder(5, 0, 5, 0)
            )
        );
        this.pathTextField_.setFont(Utility.fontHelveticaPlain);

        this.fileChooser_.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        this.add(pathLabel);
        this.add(this.pathTextField_);
        this.add(this.pathButton);
    }

    public JButton getPathButton() {
        return pathButton;
    }

    public String getPath() {
        return path;
    }

    private void updatePath(String str) {
        this.pathTextField_.setText(str);
        this.path = str.isEmpty() ? null : str;
    }

    public boolean openFileChooser() {
        // True means that user has picked a path.
        // False means otherwise
        int file = fileChooser_.showOpenDialog(this);
        if(file == JFileChooser.APPROVE_OPTION) {
            updatePath(fileChooser_.getSelectedFile().getAbsolutePath());
            return true;
        }
        return false;
    }

    public void clear() {
        updatePath("");
    }
}
