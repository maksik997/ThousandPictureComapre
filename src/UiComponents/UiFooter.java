package UiComponents;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class UiFooter extends JPanel {
    private final JButton resetButton, loadFilesButton, fileTransferButton;

    public UiFooter() {
        this.setLayout(new GridLayout(1, 3));
        this.setBorder(
            new MatteBorder(1,0,0,0, Color.GRAY)
        );

        Insets insets = new Insets(5, 15, 5, 15);

        this.resetButton = Utility.buttonFactory("Reset", insets);
        this.loadFilesButton = Utility.buttonFactory("Load files & compare", insets);
        this.fileTransferButton = Utility.buttonFactory("Move files", insets);

        clear();

        this.add(resetButton);
        this.add(loadFilesButton);
        this.add(fileTransferButton);
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getLoadFilesButton() {
        return loadFilesButton;
    }

    public JButton getFileTransferButton() {
        return fileTransferButton;
    }

    public void clear() {
        this.resetButton.setEnabled(false);
        this.loadFilesButton.setEnabled(false);
        this.fileTransferButton.setEnabled(false);
    }
}
