package pl.magzik.ui.views;

import pl.magzik.ui.components.settings.*;
import pl.magzik.ui.components.Utility;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class SettingsView extends AbstractView {

    private final JButton saveButton;

    private final ComboBoxSettingsEntry languageEntry, themeEntry;
    private final PathSettingsEntry destinationEntry;
    private final CheckBoxSettingsEntry recursiveModeEntry, pHashModeEntry, pixelByPixelModeEntry, namesLowerCaseEntry;
    private final TextFieldSettingsEntry namesPrefixEntry;

    private SettingsView(JButton saveButton, ComboBoxSettingsEntry languageEntry, ComboBoxSettingsEntry themeEntry, PathSettingsEntry destinationEntry, CheckBoxSettingsEntry recursiveModeEntry, CheckBoxSettingsEntry pHashModeEntry, CheckBoxSettingsEntry pixelByPixelModeEntry, CheckBoxSettingsEntry namesLowerCaseEntry, TextFieldSettingsEntry namesPrefixEntry) {
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
        //addPropertyChangeListeners();
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
     * Add single tab for JTabbedPane. And fill it with {@link SettingsEntry} entries.
     * @param tabPanel {@link JTabbedPane} to be added tab to.
     * @param tabTitle Tab title.
     * @param entries Single or more {@link SettingsEntry} to be added to newly created tab.
     * */
    private void addTab(JTabbedPane tabPanel, String tabTitle, SettingsEntry<?,?>... entries) {
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

        for (SettingsEntry<?,?> entry : entries) {
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
     * Adds {@link PropertyChangeListener} to all {@link SettingsEntry}.
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

    public ComboBoxSettingsEntry getLanguageEntry() {
        return languageEntry;
    }

    public ComboBoxSettingsEntry getThemeEntry() {
        return themeEntry;
    }

    public PathSettingsEntry getDestinationEntry() {
        return destinationEntry;
    }

    public CheckBoxSettingsEntry getRecursiveModeEntry() {
        return recursiveModeEntry;
    }

    public CheckBoxSettingsEntry getPHashModeEntry() {
        return pHashModeEntry;
    }

    public CheckBoxSettingsEntry getPixelByPixelModeEntry() {
        return pixelByPixelModeEntry;
    }

    public CheckBoxSettingsEntry getNamesLowerCaseEntry() {
        return namesLowerCaseEntry;
    }

    public TextFieldSettingsEntry getNamesPrefixEntry() {
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
            ComboBoxSettingsEntry languageEntry = new ComboBoxSettingsEntry("view.settings.label.language", new JComboBox<>());
            ComboBoxSettingsEntry themeEntry = new ComboBoxSettingsEntry("view.settings.label.theme", new JComboBox<>());
            PathSettingsEntry destinationEntry = new PathSettingsEntry("view.settings.label.destination", createPathPanel());
            CheckBoxSettingsEntry recursiveModeEntry = new CheckBoxSettingsEntry("view.settings.label.toggle.mode", createCheckboxPanel("view.settings.label.toggle.recursive_mode"));
            CheckBoxSettingsEntry pHashModeEntry = new CheckBoxSettingsEntry("view.settings.label.phash", createCheckboxPanel("view.settings.toggle.phash"));
            CheckBoxSettingsEntry pixelByPixelModeEntry = new CheckBoxSettingsEntry("view.settings.label.pixel_by_pixel", createCheckboxPanel("view.settings.toggle.pixel_by_pixel"));
            TextFieldSettingsEntry namesPrefixEntry = new TextFieldSettingsEntry("view.settings.label.unify_name.prefix", createTextFieldPanel());
            CheckBoxSettingsEntry namesLowerCaseEntry = new CheckBoxSettingsEntry("view.settings.label.unify_name.lower_case", createCheckboxPanel("view.settings.toggle.unify_name.lower_case"));

            return new SettingsView(
                saveButton, languageEntry,
                themeEntry, destinationEntry,
                recursiveModeEntry, pHashModeEntry,
                pixelByPixelModeEntry, namesLowerCaseEntry,
                namesPrefixEntry
            );
        }

        /**
         * Creates a panel used as a value for {@link PathSettingsEntry}.
         * */
        private static JPanel createPathPanel() {
            JPanel panel = new JPanel();
            JTextField textField = new JTextField();
            JButton button = Utility.buttonFactory("view.settings.button.destination.open", new Insets(5, 15, 5, 15));
            panel.add(textField);
            panel.add(Box.createHorizontalStrut(10));
            panel.add(button);
            return panel;
        }

        /**
         * Creates a panel used as a value for {@link CheckBoxSettingsEntry}.
         * */
        private static JPanel createCheckboxPanel(String checkboxText) {
            Objects.requireNonNull(checkboxText);

            JPanel panel = new JPanel();
            JCheckBox checkBox = new JCheckBox(checkboxText);
            panel.add(checkBox);
            return panel;
        }

        /**
         * Creates a panel used as a value for {@link TextFieldSettingsEntry}.
         * */
        private static JPanel createTextFieldPanel() {
            JPanel panel = new JPanel();
            JTextField textField = new JTextField();
            panel.add(textField);
            return panel;
        }
    }
}
