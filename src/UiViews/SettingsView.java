package UiViews;

import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;

public class SettingsView extends AbstractView {

//    private final UiPath destinationForComparer;

    private final JCheckBox recursiveModeToggle, pHashModeToggle, pixelByPixelModeToggle, unifyNamesLowerCaseToggle;

    private final JButton saveButton, destinationOpenButton;

    private final JTextField destinationTextField, unifyNamePrefixTextField;

    private final JComboBox<String> languageComboBox, themeComboBox;

    private final JFileChooser destinationFileChooser;

    public SettingsView() {
        Border labelBorder = new CompoundBorder(
            new MatteBorder(0, 0, 1, 1, Color.GRAY),
            new EmptyBorder(10, 5, 10, 5)
        );
        Border textFieldBorder = new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        );
        Border checkBoxBorder = new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(3, 5, 3, 5)
        );
        Border comboBoxBorder = new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.GRAY),
                new EmptyBorder(8, 5, 8, 5)
        );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        //gbc.insets = new Insets(5, 10, 5, 10);

        JLabel panelTitle = new JLabel("LOC_SETTINGS_VIEW_HEADER_LABEL");
        panelTitle.setFont(Utility.fontBigHelveticaBold);
        panelTitle.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        panelTitle.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(panelTitle, gbc);
        gbc.gridy++;
        gbc.weighty = 1;

        JTabbedPane settingsTabs = new JTabbedPane();

        // General settings
        JPanel generalSettingsPanel = new JPanel();
        generalSettingsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;

        JLabel languageLabel = new JLabel("LOC_SETTINGS_VIEW_LANGUAGE_LABEL");
        languageLabel.setFont(Utility.fontHelveticaPlain);
        languageLabel.setBorder(labelBorder);

        languageComboBox = new JComboBox<>();
        languageComboBox.setFont(Utility.fontHelveticaPlain);
        languageComboBox.setBorder(comboBoxBorder);

        generalSettingsPanel.add(languageLabel, c);
        c.gridx = 1;
        generalSettingsPanel.add(languageComboBox, c);
        c.gridy = 1;
        c.gridx = 0;

        JLabel themeLabel = new JLabel("LOC_SETTINGS_VIEW_THEME_LABEL");
        themeLabel.setFont(Utility.fontHelveticaPlain);
        themeLabel.setBorder(labelBorder);

        themeComboBox = new JComboBox<>();
        themeComboBox.setFont(Utility.fontHelveticaPlain);
        themeComboBox.setBorder(comboBoxBorder);

        generalSettingsPanel.add(themeLabel, c);
        c.gridx = 1;
        generalSettingsPanel.add(themeComboBox, c);
        c.gridy = 2;
        c.gridx = 0;
        c.weighty = 1;
        c.gridwidth = 2;
        generalSettingsPanel.add(new JPanel(), c);

        settingsTabs.addTab("LOC_SETTINGS_TAB_TITLE_GENERAL", generalSettingsPanel);

        // Comparer's settings
        JPanel comparerSettingsPanel = new JPanel();
        comparerSettingsPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;

        JLabel destinationLabel = new JLabel("LOC_SETTINGS_VIEW_DESTINATION_LABEL");
        destinationLabel.setFont(Utility.fontHelveticaPlain);
        destinationLabel.setBorder(labelBorder);

        comparerSettingsPanel.add(destinationLabel, c);
        c.gridx = 1;

        JPanel destinationPanel = new JPanel();
        destinationPanel.setLayout(new BoxLayout(destinationPanel, BoxLayout.X_AXIS));
        destinationPanel.setBorder(textFieldBorder);

        destinationTextField = new JTextField();
        destinationTextField.setEditable(false);
        destinationTextField.setFocusable(false);
        destinationTextField.setFont(Utility.fontHelveticaPlain);
        destinationTextField.setBorder(new CompoundBorder(
            new LineBorder(Color.GRAY, 1, true),
            new EmptyBorder(0, 5, 0, 5)
        ));
        destinationPanel.add(destinationTextField);
        destinationPanel.add(Box.createHorizontalStrut(10));

        destinationOpenButton = Utility.buttonFactory("LOC_SETTINGS_VIEW_DESTINATION_OPEN_BUTTON", new Insets(5, 15, 5, 15));
        destinationPanel.add(destinationOpenButton);
        
        comparerSettingsPanel.add(destinationPanel, c);
        c.gridy = 1;
        c.gridx = 0;

        JLabel modeToggleLabel = new JLabel("LOC_SETTINGS_VIEW_MODE_TOGGLE_LABEL");
        modeToggleLabel.setFont(Utility.fontHelveticaPlain);
        modeToggleLabel.setBorder(labelBorder);

        comparerSettingsPanel.add(modeToggleLabel, c);
        c.gridx = 1;

        JPanel modePanel = new JPanel();
        recursiveModeToggle = new JCheckBox("LOC_SETTINGS_VIEW_RECURSIVE_MODE_TOGGLE");
        recursiveModeToggle.setFont(Utility.fontHelveticaPlain);
        modePanel.add(recursiveModeToggle, c);
        modePanel.setBorder(checkBoxBorder);
        comparerSettingsPanel.add(modePanel, c);
        c.gridy = 2;
        c.gridx = 0;

        JLabel pHashModeLabel = new JLabel("LOC_SETTINGS_VIEW_PHASH_MODE_LABEL");
        pHashModeLabel.setFont(Utility.fontHelveticaPlain);
        pHashModeLabel.setBorder(labelBorder);

        JPanel pHashModePanel = new JPanel();
        pHashModePanel.setBorder(checkBoxBorder);
        pHashModeToggle = new JCheckBox("LOC_SETTINGS_VIEW_PHASH_MODE_TOGGLE");
        pHashModeToggle.setFont(Utility.fontHelveticaPlain);
        pHashModePanel.add(pHashModeToggle);

        comparerSettingsPanel.add(pHashModeLabel, c);
        c.gridx = 1;

        comparerSettingsPanel.add(pHashModePanel, c);
        c.gridy = 3;
        c.gridx = 0;

        JLabel pixelByPixelModeLabel = new JLabel("LOC_SETTINGS_VIEW_PIXEL_BY_PIXEL_MODE_LABEL");
        pixelByPixelModeLabel.setFont(Utility.fontHelveticaPlain);
        pixelByPixelModeLabel.setBorder(labelBorder);

        JPanel pixelByPixelModePanel = new JPanel();
        pixelByPixelModePanel.setBorder(checkBoxBorder);
        pixelByPixelModeToggle = new JCheckBox("LOC_SETTINGS_VIEW_PIXEL_BY_PIXEL_MODE_TOGGLE");
        pixelByPixelModeToggle.setFont(Utility.fontHelveticaPlain);
        pixelByPixelModePanel.add(pixelByPixelModeToggle);

        comparerSettingsPanel.add(pixelByPixelModeLabel, c);
        c.gridx = 1;

        comparerSettingsPanel.add(pixelByPixelModePanel, c);
        c.gridy = 4;
        c.gridx = 0;
        c.weighty = 1;
        c.gridwidth = 2;
        comparerSettingsPanel.add(new JPanel(), c);

        settingsTabs.addTab("LOC_SETTINGS_TAB_TITLE_COMPARER", /*new JScrollPane(*/comparerSettingsPanel)/*)*/;

        // Gallery's settings
        JPanel gallerySettingsPanel = new JPanel();
        gallerySettingsPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;

        JLabel unifyNamePrefixLabel = new JLabel("LOC_SETTINGS_VIEW_UNIFY_NAME_PREFIX_LABEL");
        unifyNamePrefixLabel.setFont(Utility.fontHelveticaPlain);
        unifyNamePrefixLabel.setBorder(labelBorder);

        gallerySettingsPanel.add(unifyNamePrefixLabel, c);
        c.gridx = 1;

        JPanel unifyNamePanel = new JPanel();
        unifyNamePanel.setBorder(textFieldBorder);
        unifyNamePanel.setLayout(new GridLayout(1,1));
        unifyNamePrefixTextField = new JTextField();
        unifyNamePrefixTextField.setEditable(true);
        unifyNamePrefixTextField.setFocusable(true);
        unifyNamePrefixTextField.setFont(Utility.fontHelveticaPlain);
        unifyNamePrefixTextField.setBorder(new CompoundBorder(
            new LineBorder(Color.GRAY, 1, true),
            new EmptyBorder(4, 5, 4, 5)
        ));
        unifyNamePanel.add(unifyNamePrefixTextField);

        gallerySettingsPanel.add(unifyNamePanel, c);
        c.gridy = 1;
        c.gridx = 0;

        JLabel unifyNameLowerCaseLabel = new JLabel("LOC_SETTINGS_VIEW_UNIFY_NAME_LOWER_LABEL");
        unifyNameLowerCaseLabel.setFont(Utility.fontHelveticaPlain);
        unifyNameLowerCaseLabel.setBorder(labelBorder);

        gallerySettingsPanel.add(unifyNameLowerCaseLabel, c);
        c.gridx = 1;

        JPanel unifyNamesLowerCasePanel = new JPanel();
        unifyNamesLowerCasePanel.setBorder(checkBoxBorder);
        unifyNamesLowerCaseToggle = new JCheckBox("LOC_SETTINGS_VIEW_UNIFY_NAME_LOWER_TOGGLE");
        unifyNamesLowerCaseToggle.setFont(Utility.fontHelveticaPlain);
        unifyNamesLowerCasePanel.add(unifyNamesLowerCaseToggle);

        gallerySettingsPanel.add(unifyNamesLowerCasePanel, c);
        c.gridy = 2;
        c.gridx = 0;
        c.weighty = 1;
        c.gridwidth = 2;
        gallerySettingsPanel.add(new JPanel(), c);

        settingsTabs.addTab("LOC_SETTINGS_TAB_TITLE_GALLERY", gallerySettingsPanel);

        mainPanel.add(settingsTabs, gbc);
        gbc.gridy++;

        saveButton = Utility.buttonFactory("LOC_SETTINGS_VIEW_SAVE_BUTTON", new Insets(5, 10, 5, 10));

        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1;
        mainPanel.add(saveButton, gbc);

        this.add(mainPanel);

        destinationFileChooser = new JFileChooser();
        destinationFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        destinationFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        destinationFileChooser.setDialogTitle("LOC_SETTINGS_VIEW_DESTINATION_FILE_CHOOSER_TITLE");
        destinationFileChooser.setApproveButtonText("LOC_SETTINGS_VIEW_DESTINATION_FILE_CHOOSER_APPROVE_BUTTON");
    }

    public JFileChooser getDestinationFileChooser() {
        return destinationFileChooser;
    }

    public void openDestinationFileChooser() {
        int f = destinationFileChooser.showOpenDialog(this);
        if (f == JFileChooser.APPROVE_OPTION) {
            destinationTextField.setText(destinationFileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public JButton getDestinationOpenButton() {
        return destinationOpenButton;
    }

    public JTextField getDestinationTextField() {
        return destinationTextField;
    }

    public JCheckBox getRecursiveModeToggle() {
        return recursiveModeToggle;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JComboBox<String> getLanguageComboBox() {
        return languageComboBox;
    }

    public JComboBox<String> getThemeComboBox() {
        return themeComboBox;
    }

    public JCheckBox getPHashModeToggle() {
        return pHashModeToggle;
    }

    public JCheckBox getPixelByPixelModeToggle() {
        return pixelByPixelModeToggle;
    }

    public JCheckBox getUnifyNamesLowerCaseToggle() {
        return unifyNamesLowerCaseToggle;
    }

    public JTextField getUnifyNamesPrefixTextField() {
        return unifyNamePrefixTextField;
    }
}
