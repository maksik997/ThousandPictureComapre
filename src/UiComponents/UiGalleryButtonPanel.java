package UiComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class UiGalleryButtonPanel extends JPanel {

    private final JProgressBar redundancyCheckProgressBar;

    private final JButton addImageButton, deleteImageButton, redundancyButton,
                        attachTagButton, deleteTagButton;

    private boolean isLocked;

    public UiGalleryButtonPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.setBorder(new MatteBorder(0,0,1,0, Color.GRAY));

        isLocked = false;

        redundancyCheckProgressBar = new JProgressBar(0, 100);
        redundancyCheckProgressBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        redundancyCheckProgressBar.setStringPainted(true);

        addImageButton = Utility.buttonFactory("Add", new Insets(5,15,5, 15));

        deleteImageButton = Utility.buttonFactory("Delete", new Insets(5,15,5, 15));
        deleteImageButton.setEnabled(false);

        redundancyButton = Utility.buttonFactory("Redundancy check", new Insets(5,15,5, 15));
        redundancyButton.setEnabled(false);

        attachTagButton = Utility.buttonFactory("Attach tag", new Insets(5,15,5, 15));
        attachTagButton.setEnabled(false);

        deleteTagButton = Utility.buttonFactory("Delete tag", new Insets(5,15,5, 15));
        deleteTagButton.setEnabled(false);

        this.add(redundancyCheckProgressBar);
        this.add(Box.createHorizontalGlue());

        this.add(addImageButton);
        this.add(deleteImageButton);
        this.add(redundancyButton);
        this.add(attachTagButton);
        this.add(deleteTagButton);

    }

    public JProgressBar getRedundancyCheckProgressBar() {
        return redundancyCheckProgressBar;
    }

    public JButton getAddImageButton() {
        return addImageButton;
    }

    public JButton getDeleteImageButton() {
        return deleteImageButton;
    }

    public JButton getRedundancyButton() {
        return redundancyButton;
    }

    public JButton getAttachTagButton() {
        return attachTagButton;
    }

    public JButton getDeleteTagButton() {
        return deleteTagButton;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;

        if (isLocked){
            addImageButton.setEnabled(false);
            deleteImageButton.setEnabled(false);
            redundancyButton.setEnabled(false);
            attachTagButton.setEnabled(false);
            deleteTagButton.setEnabled(false);
        }
    }
}
