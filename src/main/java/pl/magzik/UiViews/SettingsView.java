package pl.magzik.UiViews;

import pl.magzik.UiComponents.Settings.*;
import pl.magzik.UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class SettingsView extends AbstractView {

    private final JButton saveButton;

    private final UiComboBoxSettingsEntry languageEntry, themeEntry;
    private final UiPathSettingsEntry destinationEntry;
    private final UiCheckBoxSettingsEntry recursiveModeEntry, pHashModeEntry, pixelByPixelModeEntry, namesLowerCaseEntry;
    private final UiTextFieldSettingsEntry namesPrefixEntry;

    private SettingsView(JButton saveButton, UiComboBoxSettingsEntry languageEntry, UiComboBoxSettingsEntry themeEntry, UiPathSettingsEntry destinationEntry, UiCheckBoxSettingsEntry recursiveModeEntry, UiCheckBoxSettingsEntry pHashModeEntry, UiCheckBoxSettingsEntry pixelByPixelModeEntry, UiCheckBoxSettingsEntry namesLowerCaseEntry, UiTextFieldSettingsEntry namesPrefixEntry) {
        this.saveButton = saveButton;
        this.languageEntry = languageEntry;
        this.themeEntry = themeEntry;
        this.destinationEntry = destinationEntry;
        this.recursiveModeEntry = recursiveModeEntry;
        this.pHashModeEntry = pHashModeEntry;
        this.pixelByPixelModeEntry = pixelByPixelModeEntry;
        this.namesLowerCaseEntry = namesLowerCaseEntry;
        this.namesPrefixEntry = namesPrefixEntry;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel panelTitle = new JLabel("view.settings.label.header");
        panelTitle.setFont(Utility.fontBigHelveticaBold);
        panelTitle.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));
        panelTitle.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(panelTitle, gbc);
        gbc.gridy++;
        gbc.weighty = 1;

        addTabPanel(mainPanel, gbc);
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1;

        mainPanel.add(saveButton, gbc);

        add(mainPanel);
        addPropertyChangeListeners();
    }

    /**
     * Creates and adds Tab Panel.
     * @param mainPanel
     *                  {@link JPanel} Main panel to be used.
     * @param gbc
     *                  {@link GridBagConstraints} to be used for the main panel.
     * */
    private void addTabPanel(JPanel mainPanel, GridBagConstraints gbc) {
        Objects.requireNonNull(mainPanel);
        Objects.requireNonNull(gbc);

        JTabbedPane settingsTabs = new JTabbedPane();

        addTab(settingsTabs, "tab.settings.general.title", languageEntry, themeEntry);
        addTab(settingsTabs, "tab.settings.comparer.title", destinationEntry, recursiveModeEntry, pHashModeEntry, pixelByPixelModeEntry);
        addTab(settingsTabs, "tab.settings.gallery.title", namesPrefixEntry, namesLowerCaseEntry);

        mainPanel.add(settingsTabs, gbc);
        gbc.gridy++;
    }

    /**
     * Add single tab for JTabbedPane. And fill it with {@link UiSettingsEntry} entries.
     * @param tabPanel {@link JTabbedPane} to be added tab to.
     * @param tabTitle Tab title.
     * @param entries Single or more {@link UiSettingsEntry} to be added to newly created tab.
     * */
    private void addTab(JTabbedPane tabPanel, String tabTitle, UiSettingsEntry<?,?>... entries) {
        Objects.requireNonNull(tabPanel);
        Objects.requireNonNull(tabTitle);
        Objects.requireNonNull(entries);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;

        for (UiSettingsEntry<?,?> entry : entries) {
            panel.add(entry.getLabelComponent(), c);
            c.gridx = 1;
            panel.add(entry.getValueComponent(), c);
            c.gridy++;
            c.gridx = 0;
        }

        c.weighty = 1;
        c.gridwidth = 2;
        panel.add(new JPanel(), c);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));

        tabPanel.add(tabTitle, scrollPane);
    }

    /**
     * Adds {@link PropertyChangeListener} to all {@link UiSettingsEntry}.
     * <p>
     * Listener turn on the save button if any change has occurred.
     * */
    private void addPropertyChangeListeners() {
        PropertyChangeListener pcl = e -> {
            if (e.getPropertyName().equals("value") && !saveButton.isEnabled())
                saveButton.setEnabled(true);
        };

        languageEntry.addPropertyChangeListener(pcl);
        themeEntry.addPropertyChangeListener(pcl);
        destinationEntry.addPropertyChangeListener(pcl);
        recursiveModeEntry.addPropertyChangeListener(pcl);
        pHashModeEntry.addPropertyChangeListener(pcl);
        pixelByPixelModeEntry.addPropertyChangeListener(pcl);
        namesLowerCaseEntry.addPropertyChangeListener(pcl);
        namesPrefixEntry.addPropertyChangeListener(pcl);
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public UiComboBoxSettingsEntry getLanguageEntry() {
        return languageEntry;
    }

    public UiComboBoxSettingsEntry getThemeEntry() {
        return themeEntry;
    }

    public UiPathSettingsEntry getDestinationEntry() {
        return destinationEntry;
    }

    public UiCheckBoxSettingsEntry getRecursiveModeEntry() {
        return recursiveModeEntry;
    }

    public UiCheckBoxSettingsEntry getPHashModeEntry() {
        return pHashModeEntry;
    }

    public UiCheckBoxSettingsEntry getPixelByPixelModeEntry() {
        return pixelByPixelModeEntry;
    }

    public UiCheckBoxSettingsEntry getNamesLowerCaseEntry() {
        return namesLowerCaseEntry;
    }

    public UiTextFieldSettingsEntry getNamesPrefixEntry() {
        return namesPrefixEntry;
    }

    /**
     * Creates {@link SettingsView} using Factory Method Pattern.
     * */
    public static class Factory {

        /**
         * Creates {@link SettingsView} class using {@link SettingsView}'s private constructor.
         * @return {@link SettingsView} class.
         * */
        public static SettingsView create() {
            JButton saveButton = Utility.buttonFactory("view.settings.button.save", new Insets(5, 10, 5, 10));
            UiComboBoxSettingsEntry languageEntry = new UiComboBoxSettingsEntry("view.settings.label.language", new JComboBox<>());
            UiComboBoxSettingsEntry themeEntry = new UiComboBoxSettingsEntry("view.settings.label.theme", new JComboBox<>());
            UiPathSettingsEntry destinationEntry = new UiPathSettingsEntry("view.settings.label.destination", createPathPanel("view.settings.button.destination.open"));
            UiCheckBoxSettingsEntry recursiveModeEntry = new UiCheckBoxSettingsEntry("view.settings.label.toggle.mode", createCheckboxPanel("view.settings.label.toggle.recursive_mode"));
            UiCheckBoxSettingsEntry pHashModeEntry = new UiCheckBoxSettingsEntry("view.settings.label.phash", createCheckboxPanel("view.settings.toggle.phash"));
            UiCheckBoxSettingsEntry pixelByPixelModeEntry = new UiCheckBoxSettingsEntry("view.settings.label.pixel_by_pixel", createCheckboxPanel("view.settings.toggle.pixel_by_pixel"));
            UiTextFieldSettingsEntry namesPrefixEntry = new UiTextFieldSettingsEntry("view.settings.label.unify_name.prefix", createTextFieldPanel());
            UiCheckBoxSettingsEntry namesLowerCaseEntry = new UiCheckBoxSettingsEntry("view.settings.label.unify_name.lower_case", createCheckboxPanel("view.settings.toggle.unify_name.lower_case"));

            return new SettingsView(
                saveButton, languageEntry,
                themeEntry, destinationEntry,
                recursiveModeEntry, pHashModeEntry,
                pixelByPixelModeEntry, namesLowerCaseEntry,
                namesPrefixEntry
            );
        }

        /**
         * Creates a panel used as a value for {@link UiPathSettingsEntry}.
         * */
        private static JPanel createPathPanel(String buttonText) {
            Objects.requireNonNull(buttonText);

            JPanel panel = new JPanel();
            JTextField textField = new JTextField();
            JButton button = Utility.buttonFactory(buttonText, new Insets(5, 15, 5, 15));
            panel.add(textField);
            panel.add(Box.createHorizontalStrut(10));
            panel.add(button);
            return panel;
        }

        /**
         * Creates a panel used as a value for {@link UiCheckBoxSettingsEntry}.
         * */
        private static JPanel createCheckboxPanel(String checkboxText) {
            Objects.requireNonNull(checkboxText);

            JPanel panel = new JPanel();
            JCheckBox checkBox = new JCheckBox(checkboxText);
            panel.add(checkBox);
            return panel;
        }

        /**
         * Creates a panel used as a value for {@link UiTextFieldSettingsEntry}.
         * */
        private static JPanel createTextFieldPanel() {
            JPanel panel = new JPanel();
            JTextField textField = new JTextField();
            panel.add(textField);
            return panel;
        }
    }
}
