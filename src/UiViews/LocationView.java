package UiViews;

import UiComponents.*;

import javax.swing.*;
import java.awt.*;

public class LocationView extends JPanel {

    private final UiHeader uiHeader_;

    private final UiMainPanel uiMainPanel_;

    private final UiFooter uiFooter_;
    
    public LocationView() {
        this.setLayout(new BorderLayout());

        // update v0.3
        this.uiHeader_ = new UiHeader();
        this.uiMainPanel_ = new UiMainPanel();
        this.uiFooter_ = new UiFooter();

        this.add(uiHeader_, BorderLayout.NORTH);
        this.add(uiMainPanel_);
        this.add(uiFooter_, BorderLayout.SOUTH);
    }

    public void clear(){
        uiFooter_.clear();
        uiMainPanel_.clear();
    }

    // Couple of easy access methods :)
    public boolean openFileChooser() {
        return uiMainPanel_.openFileChooser();
    }

    public void write(String msg) {
        uiMainPanel_.write(msg);
    }

    public void writeLine(String msg) {
        write(String.format("%s%n", msg));
    }

    public void updateTray(long total, long processed, long duplicates) {
        setTotalFieldValue(total);
        setProcessedFieldValue(processed);
        setDuplicatesFieldValue(duplicates);
    }

    public void setTotalFieldValue(long value) {
        uiMainPanel_.getUiTray_().getTotalField_().setText(
            String.valueOf(value)
        );
    }

    public void setProcessedFieldValue(long value) {
        uiMainPanel_.getUiTray_().getProcessedField_().setText(
            String.valueOf(value)
        );
    }

    public void setDuplicatesFieldValue(long value) {
        uiMainPanel_.getUiTray_().getDuplicatesField_().setText(
            String.valueOf(value)
        );
    }

    public JButton getButton(Utility.Buttons button){
        return switch (button) {
            case SETTINGS -> uiHeader_.getSettingsButton();
            case OPEN_SOURCE -> uiMainPanel_.getPathButton();
            case RESET -> uiFooter_.getResetButton();
            case LOAD_FILES -> uiFooter_.getLoadFilesButton();
            case MOVE_FILES -> uiFooter_.getFileTransferButton();
        };
    }

    public String getPath() {
        return uiMainPanel_.getPath();
    }

    public UiHeader getUiHeader_() {
        return uiHeader_;
    }

    public UiMainPanel getUiMainPanel_() {
        return uiMainPanel_;
    }

    public UiFooter getUiFooter_() {
        return uiFooter_;
    }
}
