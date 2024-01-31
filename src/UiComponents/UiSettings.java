package UiComponents;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class UiSettings extends JPanel {

    private final UiPath uiPath_;

    public UiSettings() {
        this.setLayout(new GridBagLayout());
        this.setBorder(new TitledBorder(
            new LineBorder(Color.GRAY, 2),
            "Settings",
            TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
            Utility.fontBigHelveticaBold
        ));

        this.uiPath_ = new UiPath();

        this.uiPath_.setBorder(new TitledBorder(
            new LineBorder(Color.GRAY, 2),
            "Destination directory:",
            TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
            Utility.fontSmallHelveticaBold
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 1.d;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.d;

        this.add(uiPath_, gbc);
    }

    // Couple of easy access methods :)
    public String getPath() {
        return uiPath_.getPath();
    }

    public boolean openFileChooser() {
        return uiPath_.openFileChooser();
    }

    public JButton getPathButton() {
        return uiPath_.getPathButton();
    }
}
