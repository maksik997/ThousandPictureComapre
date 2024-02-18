package UiComponents;

import javax.swing.*;
import java.awt.*;

public class UiMainPanel extends JPanel {

    private final UiPath uiPath_;

    private final UiOutput uiOutput_;

    private final UiTray uiTray_;

    public UiMainPanel() {
        this.setLayout(new BorderLayout());

        this.uiPath_ = new UiPath();
        this.uiOutput_ = new UiOutput();
        this.uiTray_ = new UiTray();

        this.add(uiPath_, BorderLayout.PAGE_START);
        this.add(uiTray_, BorderLayout.LINE_START);
        this.add(uiOutput_);
    }

    public void clear() {
        uiPath_.clear();
        uiOutput_.clear();
        uiTray_.clear();
    }

    // Couple of easy access methods :)
    public boolean openFileChooser() {
        return uiPath_.openFileChooser();
    }

    public void write(String msg) {
        uiOutput_.write(msg);
    }

    public void writeLine(String msg) {
        write(String.format("%s%n", msg));
    }

    public void updateTray(long total, long processed, long duplicates) {
        uiTray_.update(total, processed, duplicates);
    }

    public String getPath() {
        return uiPath_.getPath();
    }

    public JButton getPathButton() {
        return uiPath_.getPathButton();
    }
}
