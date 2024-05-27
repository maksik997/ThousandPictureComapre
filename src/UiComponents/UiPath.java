package UiComponents;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;

public class UiPath extends JPanel {
    private String path;
    private final JTextField pathTextField;
    private final JButton pathButton;
    private final JFileChooser fileChooser;

    public UiPath() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0, Color.GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));

        this.pathTextField = new JTextField();
        this.pathTextField.setEditable(false);
        this.pathTextField.setFocusable(false);
        this.pathTextField.setBorder(
            new TitledBorder(
                new CompoundBorder(
                    new LineBorder(Color.GRAY, 1, true),
                    new EmptyBorder(5, 10, 0, 10)
                ),
                "Path:"
            )
        );
        this.pathTextField.setFont(Utility.fontHelveticaPlain);

        this.pathButton = Utility.buttonFactory(
            "Open",
            new Insets(5, 15, 5, 15)
        );

        this.fileChooser = new JFileChooser();
        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        this.fileChooser.setDialogTitle("Pick a directory with pictures to compare:");
        this.fileChooser.setApproveButtonText("Pick");

        this.add(this.pathTextField);
        this.add(Box.createHorizontalStrut(40));
        this.add(this.pathButton);
    }

    public JButton getPathButton() {
        return pathButton;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String str) {
        this.pathTextField.setText(str);
        this.path = str.isEmpty() ? null : str;
    }

    public void clearPath() {
        setPath("");
    }

    public boolean openFileChooser() {
        // True means that user has picked a path.
        // False means otherwise
        int file = fileChooser.showOpenDialog(this);
        if(file == JFileChooser.APPROVE_OPTION) {
            setPath(fileChooser.getSelectedFile().getAbsolutePath());
            return true;
        }
        return false;
    }
}
