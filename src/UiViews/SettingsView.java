package UiViews;

import UiComponents.UiPath;
import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SettingsView extends AbstractView {

    //private final UiSettings uiSettings_;

    private final UiPath destinationForComparer;

    private final JCheckBox recursiveModeToggle;

    private final JButton saveButton;

    public SettingsView() {
        //super.uiHeader_.toggleButton(UiHeader.Button.SETTINGS);

        //this.uiSettings_ = new UiSettings();

//        this.add(uiSettings_);
        Border botBorder = new MatteBorder(0, 0, 1, 0, Color.GRAY);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel headerLabel = new JLabel("Settings");
        headerLabel.setFont(Utility.fontBigHelveticaBold);
        headerLabel.setBorder(botBorder);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        settingsPanel.add(headerLabel, gbc);
        gbc.gridy++;

        JLabel destinationLabel = new JLabel("Destination for Comparer output:");
        destinationLabel.setFont(Utility.fontHelveticaPlain);
        destinationLabel.setBorder(botBorder);

        settingsPanel.add(destinationLabel, gbc);
        gbc.gridy++;

        destinationForComparer = new UiPath();
        settingsPanel.add(destinationForComparer, gbc);
        gbc.gridy++;

        JLabel modeToggleLabel = new JLabel("Mode toggle:");
        modeToggleLabel.setFont(Utility.fontHelveticaPlain);
        modeToggleLabel.setBorder(botBorder);

        settingsPanel.add(modeToggleLabel, gbc);
        gbc.gridy++;

        recursiveModeToggle = new JCheckBox("Recursive mode");
        recursiveModeToggle.setFont(Utility.fontHelveticaPlain);
        recursiveModeToggle.setBorder(botBorder);
        settingsPanel.add(recursiveModeToggle, gbc);
        gbc.gridy++;

        saveButton = Utility.buttonFactory("Save settings", new Insets(5, 10, 5, 10));

        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1;
        settingsPanel.add(saveButton, gbc);

        this.add(settingsPanel);
    }

    // Couple of easy access methods :)
//    public String getPath() {
//        return uiSettings_.getPath();
//    }
//
//    public boolean openFileChooser() {
//        return uiSettings_.openFileChooser();
//    }
//
//    public JButton getPathButton() {
//        return uiSettings_.getPathButton();
//    }
//
//    public JComboBox<Comparer.Modes> getModeSelector() {
//        return uiSettings_.getModeComboBox();
//    }


    public UiPath getDestinationForComparer() {
        return destinationForComparer;
    }

    public JCheckBox getRecursiveModeToggle() {
        return recursiveModeToggle;
    }

    public JButton getSaveButton() {
        return saveButton;
    }
}
