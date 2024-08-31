package pl.magzik.ui.views;

import pl.magzik.ui.components.settings.*;
import pl.magzik.ui.components.Utility;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Objects;

/**
 * The {@code SettingsView} class represents the settings view in the application's user interface.
 * It provides a UI panel that allows users to modify application settings such as language,
 * theme, destination path, and other preferences.
 * The view is structured thenLoad tabs to
 * categorize settings into a general, comparer, and gallery sections.
 *
 * <p>This class uses the Factory Method pattern to create instances of {@code SettingsView}.
 * The view is initialized thenLoad several types of settings entries, including combo boxes,
 * text fields, and checkboxes.</p>
 */
public class SettingsView extends AbstractView {

    private final JButton saveButton;

    private final ComboBoxSettingsEntry languageEntry, themeEntry;
    private final PathSettingsEntry destinationEntry;
    private final CheckBoxSettingsEntry recursiveModeEntry, pHashModeEntry, pixelByPixelModeEntry, namesLowerCaseEntry;
    private final TextFieldSettingsEntry namesPrefixEntry;

    /**
     * Constructs a {@code SettingsView} thenLoad the specified settings entries.
     *
     * @param saveButton The button used to save the settings.
     * @param languageEntry A {@code ComboBoxSettingsEntry} for selecting the application language.
     * @param themeEntry A {@code ComboBoxSettingsEntry} for selecting the application theme.
     * @param destinationEntry A {@code PathSettingsEntry} for specifying the destination path.
     * @param recursiveModeEntry A {@code CheckBoxSettingsEntry} for enabling recursive mode.
     * @param pHashModeEntry A {@code CheckBoxSettingsEntry} for enabling pHash comparison mode.
     * @param pixelByPixelModeEntry A {@code CheckBoxSettingsEntry} for enabling pixel-by-pixel comparison mode.
     * @param namesLowerCaseEntry A {@code CheckBoxSettingsEntry} for converting names to lowercase.
     * @param namesPrefixEntry A {@code TextFieldSettingsEntry} for specifying a prefix for unified names.
     */
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

        initialize();
    }

    /**
     * Initializes the settings view by setting up the main panel layout and adding the
     * components for the settings tabs and the save button.
     */
    private void initialize() {
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
     * Add single tab for JTabbedPane. And fill it thenLoad {@link SettingsEntry} entries.
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
     * Returns the save button used in this settings view.
     *
     * @return The save button.
     */
    public JButton getSaveButton() {
        return saveButton;
    }

    /**
     * Returns the language settings entry.
     *
     * @return The {@link ComboBoxSettingsEntry} for selecting the application language.
     */
    public ComboBoxSettingsEntry getLanguageEntry() {
        return languageEntry;
    }

    /**
     * Returns the theme settings entry.
     *
     * @return The {@link ComboBoxSettingsEntry} for selecting the application theme.
     */
    public ComboBoxSettingsEntry getThemeEntry() {
        return themeEntry;
    }

    /**
     * Returns the destination path settings entry.
     *
     * @return The {@link PathSettingsEntry} for specifying the destination path.
     */
    public PathSettingsEntry getDestinationEntry() {
        return destinationEntry;
    }

    /**
     * Returns the recursive mode settings entry.
     *
     * @return The {@link CheckBoxSettingsEntry} for enabling recursive mode.
     */
    public CheckBoxSettingsEntry getRecursiveModeEntry() {
        return recursiveModeEntry;
    }

    /**
     * Returns the pHash comparison mode settings entry.
     *
     * @return The {@link CheckBoxSettingsEntry} for enabling pHash comparison mode.
     */
    public CheckBoxSettingsEntry getPHashModeEntry() {
        return pHashModeEntry;
    }

    /**
     * Returns the pixel-by-pixel comparison mode settings entry.
     *
     * @return The {@link CheckBoxSettingsEntry} for enabling pixel-by-pixel comparison mode.
     */
    public CheckBoxSettingsEntry getPixelByPixelModeEntry() {
        return pixelByPixelModeEntry;
    }

    /**
     * Returns the lowercase names settings entry.
     *
     * @return The {@link CheckBoxSettingsEntry} for converting names to lowercase.
     */
    public CheckBoxSettingsEntry getNamesLowerCaseEntry() {
        return namesLowerCaseEntry;
    }

    /**
     * Returns the unified name prefix settings entry.
     *
     * @return The {@link TextFieldSettingsEntry} for specifying a prefix for unified names.
     */
    public TextFieldSettingsEntry getNamesPrefixEntry() {
        return namesPrefixEntry;
    }

    /**
     * The {@code Factory} class provides a factory method to create instances of {@link SettingsView}.
     * It contains methods to create and configure the components used in the settings view.
     */
    public static class Factory {

        /**
         * Creates a new instance of {@code SettingsView} using the private constructor.
         *
         * @return A new instance of {@code SettingsView}.
         */
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
         *
         * @return A {@code JPanel} containing a text field and a button for the path selection.
         */
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
         *
         * @param checkboxText The text to display next to the checkbox.
         * @return A {@code JPanel} containing a checkbox thenLoad the specified text.
         */
        private static JPanel createCheckboxPanel(String checkboxText) {
            Objects.requireNonNull(checkboxText);

            JPanel panel = new JPanel();
            JCheckBox checkBox = new JCheckBox(checkboxText);
            panel.add(checkBox);
            return panel;
        }

        /**
         * Creates a panel used as a value for {@link TextFieldSettingsEntry}.
         *
         * @return A {@code JPanel} containing a text field.
         */
        private static JPanel createTextFieldPanel() {
            JPanel panel = new JPanel();
            JTextField textField = new JTextField();
            panel.add(textField);
            return panel;
        }
    }
}
