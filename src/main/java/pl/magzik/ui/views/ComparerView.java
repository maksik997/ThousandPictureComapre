package pl.magzik.ui.views;

import pl.magzik.ui.components.Utility;
import pl.magzik.ui.components.filechoosers.FileChooser;
import pl.magzik.ui.components.filechoosers.SingleFileSelectionStrategy;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The {@code ComparerView} class represents a user interface for comparing files.
 * <p>
 * It contains components for selecting a directory, loading and managing files, and displaying
 * results such as found and duplicate items.
 * </p>
 * <p>This class uses the Factory Method pattern to create instances of {@code ComparerView}.
 * The view is initialized with several types of settings entries, including combo boxes,
 * text fields, and checkboxes.</p>
 */
public class ComparerView extends AbstractView implements PropertyChangeListener {

    private final JTextField pathTextField;
    private final JButton pathButton;
    private final FileChooser<String> fileChooser;
    private final JButton loadButton, moveButton, resetButton;
    private final JLabel statusLabel;
    private final JList<String> foundList, duplicateList;
    private final JTextField totalFoundTextField, duplicateFoundTextField;

    /**
     * Constructs a {@code ComparerView} with the specified components.
     *
     * @param pathTextField The text field displaying the selected path.
     * @param pathButton The button for opening a file chooser.
     * @param fileChooser The file chooser used for selecting directories.
     * @param loadButton The button for loading files.
     * @param moveButton The button for moving files.
     * @param resetButton The button for resetting the view.
     * @param statusLabel The label displaying the status of the view.
     * @param foundList The list showing found items.
     * @param duplicateList The list showing duplicate items.
     * @param totalFoundTextField The text field displaying the total number of found items.
     * @param duplicateFoundTextField The text field displaying the number of duplicate items.
     */
    private ComparerView(JTextField pathTextField, JButton pathButton, FileChooser<String> fileChooser, JButton loadButton, JButton moveButton, JButton resetButton, JLabel statusLabel, JList<String> foundList, JList<String> duplicateList, JTextField totalFoundTextField, JTextField duplicateFoundTextField) {
        this.pathTextField = pathTextField;
        this.pathButton = pathButton;
        this.fileChooser = fileChooser;
        this.loadButton = loadButton;
        this.moveButton = moveButton;
        this.resetButton = resetButton;
        this.statusLabel = statusLabel;
        this.foundList = foundList;
        this.duplicateList = duplicateList;
        this.totalFoundTextField = totalFoundTextField;
        this.duplicateFoundTextField = duplicateFoundTextField;

        initialize();

        resetButton.setEnabled(false);
        moveButton.setEnabled(false);
    }

    /**
     * Initializes the user interface components and layout.
     */
    private void initialize() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createPathPanel(), BorderLayout.PAGE_START);
        mainPanel.add(createRightPanel(), BorderLayout.LINE_END);
        mainPanel.add(createContentPanel());

        add(mainPanel);
    }

    /**
     * Creates and configures the panel for the path section.
     *
     * @return A {@code JPanel} containing the path text field and button.
     */
    private JPanel createPathPanel() {
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));
        pathPanel.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0, Color.GRAY),
            new EmptyBorder(5, 10, 5, 10)
        ));

        pathPanel.add(pathTextField);
        pathPanel.add(Box.createHorizontalStrut(40));
        pathPanel.add(pathButton);

        return pathPanel;
    }

    /**
     * Creates and configures the right panel, which includes buttons and status label.
     *
     * @return A {@code JPanel} containing the button and status panels.
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.setBorder(new MatteBorder(0, 1, 0, 0, Color.GRAY));

        rightPanel.add(createButtonPanel());
        rightPanel.add(createStatusPanel());

        return rightPanel;
    }

    /**
     * Creates and configures the button panel with fileLoad, moveFiles, and release buttons.
     *
     * @return A {@code JPanel} containing the buttons for loading, moving, and resetting.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        buttonPanel.add(loadButton, gbc);
        gbc.gridy++;
        buttonPanel.add(moveButton, gbc);
        gbc.gridy++;
        buttonPanel.add(resetButton, gbc);
        gbc.gridy++;
        gbc.weighty = 1;
        buttonPanel.add(Box.createVerticalGlue(), gbc);

        return buttonPanel;
    }

    /**
     * Creates and configures the status panel with a label displaying the current status.
     *
     * @return A {@code JPanel} containing the status label.
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout());
        statusPanel.setBorder(new TitledBorder(
            new MatteBorder(1, 0, 0, 0, Color.GRAY),
            "view.comparer.state.border.title"
        ));
        statusPanel.add(statusLabel);

        return statusPanel;
    }

    /**
     * Creates and configures the content panel that contains the tray and output panels.
     *
     * @return A {@code JPanel} containing the tray and output panels.
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        contentPanel.add(createTrayPanel(), BorderLayout.NORTH);
        contentPanel.add(createOutputPanel());

        return contentPanel;
    }

    /**
     * Creates and configures the tray panel displaying total and duplicate counts.
     *
     * @return A {@code JPanel} containing the total and duplicate count text fields.
     */
    private JPanel createTrayPanel() {
        JPanel trayPanel = new JPanel();
        trayPanel.setLayout(new BoxLayout(trayPanel, BoxLayout.X_AXIS));

        trayPanel.add(totalFoundTextField);
        trayPanel.add(Box.createHorizontalGlue());
        trayPanel.add(duplicateFoundTextField);

        return trayPanel;
    }

    /**
     * Creates and configures the output panel with tabs for found and duplicate items.
     *
     * @return A {@code JPanel} containing a tabbed pane with found and duplicate item lists.
     */
    private JPanel createOutputPanel() {
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        addTab(tabbedPane, "view.comparer.tab.mapped_objects.title", foundList);
        addTab(tabbedPane, "view.comparer.tab.duplicates.title", duplicateList);
        outputPanel.add(tabbedPane);

        return outputPanel;
    }

    /**
     * Adds a new tab to the specified {@code JTabbedPane} with the given title and list.
     *
     * @param tabbedPane The {@code JTabbedPane} to which the tab will be added.
     * @param title The title of the tab.
     * @param list The {@code JList<String>} to be displayed in the tab.
     */
    private void addTab(JTabbedPane tabbedPane, String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(list));

        tabbedPane.addTab(title, panel);
    }

    /**
     * Clears the text field and updates the tray with zero counts.
     */
    public void clear() {
        pathTextField.setText(null);
        updateTray(0, 0);
    }

    /**
     * Updates the tray with the specified total and duplicate counts.
     *
     * @param total The total number of items found.
     * @param duplicates The number of duplicate items.
     */
    public void updateTray(int total, int duplicates) {
        totalFoundTextField.setText(String.valueOf(total));
        duplicateFoundTextField.setText(String.valueOf(duplicates));
    }

    /**
     * Gets the list of found items.
     *
     * @return The {@code JList<String>} containing found items.
     */
    public JList<String> getFoundList() {
        return foundList;
    }

    /**
     * Gets the list of duplicate items.
     *
     * @return The {@code JList<String>} containing duplicate items.
     */
    public JList<String> getDuplicateList() {
        return duplicateList;
    }

    /**
     * Gets the release button.
     *
     * @return The {@code JButton} for resetting the view.
     */
    public JButton getResetButton() {
        return resetButton;
    }

    /**
     * Gets the fileLoad button.
     *
     * @return The {@code JButton} for loading files.
     */
    public JButton getLoadButton() {
        return loadButton;
    }

    /**
     * Gets the moveFiles button.
     *
     * @return The {@code JButton} for moving files.
     */
    public JButton getMoveButton() {
        return moveButton;
    }

    /**
     * Gets the path text field.
     *
     * @return The {@code JTextField} displaying the selected path.
     */
    public JTextField getPathTextField() {
        return pathTextField;
    }

    /**
     * Gets the path button.
     *
     * @return The {@code JButton} for opening the file chooser.
     */
    public JButton getPathButton() {
        return pathButton;
    }

    /**
     * Gets the file chooser.
     *
     * @return The {@code FileChooser} for selecting directories.
     */
    public FileChooser<String> getFileChooser() {
        return fileChooser;
    }

    /**
     * Gets the status label.
     *
     * @return The {@code JLabel} displaying the status of the view.
     */
    public JLabel getStatusLabel() {
        return statusLabel;
    }

    /**
     * Disables all buttons associated with destructive actions.
     * <p>
     * This includes the path button, fileLoad button, moveFiles button, and release button.
     * </p>
     */
    public void blockDestructiveButtons() {
        pathButton.setEnabled(false);
        loadButton.setEnabled(false);
        moveButton.setEnabled(false);
        resetButton.setEnabled(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("comparer-processing")) {
            SwingUtilities.invokeLater(() -> {
                boolean val = (boolean) evt.getNewValue();
                if (val) blockDestructiveButtons();
                else {
                    loadButton.setEnabled(true);
                    pathButton.setEnabled(true);
                }
            });
        }
    }

    /**
     * The {@code Factory} class provides methods to create and configure instances of {@link ComparerView}.
     */
    public static class Factory {

        /**
         * Creates and configures a new instance of {@code ComparerView}.
         *
         * @return A fully configured {@code ComparerView} instance.
         */
        public static ComparerView create() {
            Insets buttonInsets = new Insets(5, 15, 5, 15);

            JTextField pathTextField = createTextField();
            JButton pathButton = Utility.buttonFactory("view.comparer.button.open", buttonInsets);
            FileChooser<String> fileChooser = createFileChooser(pathButton, pathTextField::setText);
            JButton loadButton = Utility.buttonFactory("view.comparer.button.load", buttonInsets);
            JButton moveButton = Utility.buttonFactory("view.comparer.button.move", buttonInsets);
            JButton resetButton = Utility.buttonFactory("view.comparer.button.reset", buttonInsets);
            JLabel statusLabel = createStatusLabel();
            JList<String> foundList = createList();
            JList<String> duplicateList = createList();
            JTextField totalFoundTextField = Utility.constTextFieldFactory("view.comparer.tray.total.border.title", "0", 6);
            JTextField duplicateFoundTextField = Utility.constTextFieldFactory("view.comparer.tray.duplicates.border.title", "0", 6);

            return new ComparerView(
                pathTextField,
                pathButton,
                fileChooser,
                loadButton,
                moveButton,
                resetButton,
                statusLabel,
                foundList,
                duplicateList,
                totalFoundTextField,
                duplicateFoundTextField
            );
        }

        /**
         * Creates and configures a {@link JTextField} for displaying the selected path.
         *
         * @return A {@code JTextField} with specific properties for path display.
         */
        private static JTextField createTextField() {
            JTextField textField = new JTextField();

            textField.setEditable(false);
            textField.setFocusable(false);
            textField.setBorder(
                new TitledBorder(
                    new CompoundBorder(
                        new LineBorder(Color.GRAY, 1, true),
                        new EmptyBorder(5, 10, 0, 10)
                    ),
                    "view.comparer.path.border.title"
                )
            );
            textField.setFont(Utility.fontHelveticaPlain);

            return textField;
        }

        /**
         * Creates and configures a {@link FileChooser} for selecting directories.
         *
         * @param openButton The button to open the file chooser.
         * @param consumer A {@link Consumer} to handle the selected path.
         * @return A {@code FileChooser} instance.
         */
        private static FileChooser<String> createFileChooser(JButton openButton, Consumer<String> consumer) {
            Objects.requireNonNull(openButton);
            Objects.requireNonNull(consumer);

            FileChooser<String> fc = new FileChooser<>(
                "view.comparer.file_chooser.title",
                openButton,
                consumer,
                new SingleFileSelectionStrategy()
            );
            fc.getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            return fc;
        }

        /**
         * Creates and configures a {@link JLabel} for displaying the status of the view.
         *
         * @return A {@code JLabel} with specific properties for status display.
         */
        private static JLabel createStatusLabel() {
            JLabel label = new JLabel("comparer.state.ready");
            label.setFont(Utility.fontBigHelveticaBold);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);

            return label;
        }

        /**
         * Creates and configures a {@link JList} for displaying a list of items.
         *
         * @return A {@code JList<String>} with default settings.
         */
        private static JList<String> createList() {
            JList<String> list = new JList<>();
            list.setFocusable(false);
            return list;
        }
    }
}
