package pl.magzik.ui.views;

import pl.magzik.Controller;
import pl.magzik.modules.gallery.GalleryCoordinator;
import pl.magzik.modules.gallery.table.GalleryTableModel;
import pl.magzik.ui.components.ComponentUtils;
import pl.magzik.ui.components.filechoosers.FileChooser;
import pl.magzik.ui.components.filechoosers.MultipleFileSelectionStrategy;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The {@code GalleryView} class is a UI component representing a gallery interface in the application.
 * It allows users to manage a collection of images by adding, removing, and filtering them.
 * The class integrates various UI elements, including a table for displaying images,
 * buttons for interacting thenLoad the gallery, and a file chooser for selecting files.
 * <p>
 * This class is designed to be instantiated via its nested {@link Factory} class.
 */
public class GalleryView extends AbstractView implements PropertyChangeListener {

    private final JTable galleryTable;
    private TableRowSorter<GalleryTableModel> galleryRowSorter;
    private final JLabel elementCountLabel;
    private final JTextField nameFilterTextField;
    private final JButton addImageButton, removeImageButton, deleteImageButton, distinctButton, unifyNamesButton, openButton, addTagButton, removeTagButton;
    private final JButton[] buttons;
    private FileChooser<List<String>> fileChooser;

    /**
     * Constructs a new {@code GalleryView} thenLoad the specified UI components.
     *
     * @param galleryTable         The table displaying the gallery images.
     * @param elementCountLabel    The label showing the count of elements in the gallery.
     * @param nameFilterTextField  The text field used to filter images by name.
     * @param addImageButton       The button for adding images to the gallery.
     * @param removeImageButton    The button for removing selected images from the gallery.
     * @param deleteImageButton    The button for deleting images from the system.
     * @param distinctButton       The button for removing duplicate images from the gallery.
     * @param unifyNamesButton     The button for unifying the names of images in the gallery.
     * @param openButton           The button for opening selected images.
     * @param addTagButton         The button for adding tags to selected images.
     * @param removeTagButton      The button for removing tags from selected images.
     */
    private GalleryView(JTable galleryTable, JLabel elementCountLabel, JTextField nameFilterTextField, JButton addImageButton, JButton removeImageButton, JButton deleteImageButton, JButton distinctButton, JButton unifyNamesButton, JButton openButton, JButton addTagButton, JButton removeTagButton) {
        this.galleryTable = galleryTable;
        this.elementCountLabel = elementCountLabel;
        this.nameFilterTextField = nameFilterTextField;
        this.addImageButton = addImageButton;
        this.removeImageButton = removeImageButton;
        this.deleteImageButton = deleteImageButton;
        this.distinctButton = distinctButton;
        this.unifyNamesButton = unifyNamesButton;
        this.openButton = openButton;
        this.addTagButton = addTagButton;
        this.removeTagButton = removeTagButton;
        this.buttons = new JButton[]{
            addImageButton,
            removeImageButton,
            deleteImageButton,
            distinctButton,
            unifyNamesButton,
            addTagButton,
            removeTagButton,
            openButton
        };

        initialize();
    }

    /**
     * Initializes the gallery view by setting up the main panel and adding subcomponents.
     */
    private void initialize() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.EAST);
        mainPanel.add(createTablePanel());

        add(mainPanel);
    }

    /**
     * Creates and configures the header panel containing the filter field and label.
     *
     * @return The constructed header panel.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        headerPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        c.insets = new Insets(5, 2, 5, 2);

        headerPanel.add(createLabel("view.gallery.label.header", ComponentUtils.fontHelveticaBold), c);

        c.gridx++;
        c.weightx = 1;
        headerPanel.add(Box.createHorizontalGlue(), c);

        c.gridx++;
        c.weightx = 0;
        headerPanel.add(createLabel("view.gallery.label.name_filter", ComponentUtils.fontHelveticaPlain), c);

        c.gridx++;
        headerPanel.add(nameFilterTextField, c);

        return headerPanel;
    }

    /**
     * Creates a JLabel thenLoad the specified text and font.
     *
     * @param text The text of the label.
     * @param font The font to be used for the label.
     * @return The constructed JLabel.
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);

        return label;
    }

    /**
     * Creates a JLabel thenLoad the specified text, font, and border.
     *
     * @param text   The text of the label.
     * @param font   The font to be used for the label.
     * @param border The border to be applied to the label.
     * @return The constructed JLabel.
     */
    private JLabel createLabel(String text, Font font, Border border) {
        JLabel label = createLabel(text, font);
        label.setBorder(border);

        return label;
    }

    /**
     * Creates and configures the panel containing the action buttons.
     *
     * @return The constructed button panel.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new MatteBorder(0, 1, 0, 0, Color.GRAY));
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 0;
        gbc.gridx = 0;

        for (JButton button : buttons) {
            buttonPanel.add(button, gbc);
            gbc.gridy++;
        }

        gbc.weighty = 1;
        buttonPanel.add(Box.createVerticalGlue(), gbc);

        return buttonPanel;
    }

    /**
     * Creates and configures the panel containing the table displaying the gallery images.
     *
     * @return The constructed table panel.
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        tablePanel.add(new JScrollPane(galleryTable));
        tablePanel.add(createElementCountPanel());

        return tablePanel;
    }

    /**
     * Creates and configures the panel displaying the count of elements in the gallery.
     *
     * @return The constructed element count panel.
     */
    private JPanel createElementCountPanel() {
        JPanel elementCountPanel = new JPanel();
        elementCountPanel.setLayout(new BoxLayout(elementCountPanel, BoxLayout.X_AXIS));

        elementCountPanel.add(createLabel("view.gallery.label.element_count", ComponentUtils.fontSmallHelveticaBold, new EmptyBorder(0,5,0,5)));
        elementCountPanel.add(elementCountLabel);
        elementCountPanel.add(Box.createHorizontalGlue());

        return elementCountPanel;
    }

    public JButton getAddImageButton() {
        return addImageButton;
    }

    public JButton getRemoveImageButton() {
        return removeImageButton;
    }

    public JButton getDeleteImageButton() {
        return deleteImageButton;
    }

    public JButton getDistinctButton() {
        return distinctButton;
    }

    public JButton getUnifyNamesButton() {
        return unifyNamesButton;
    }

    public JButton getOpenButton() {
        return openButton;
    }

    public JButton getAddTagButton() {
        return addTagButton;
    }

    public JButton getRemoveTagButton() {
        return removeTagButton;
    }

    public JTable getGalleryTable() {
        return galleryTable;
    }

    public JLabel getElementCountLabel() {
        return elementCountLabel;
    }

    public JTextField getNameFilterTextField() {
        return nameFilterTextField;
    }

    public FileChooser<List<String>> getFileChooser() {
        return fileChooser;
    }

    /**
     * Retrieves the currently selected rows from the gallery table and thenLoad clears the selection.
     *
     * <p>This method first gets the list of currently selected row indices from the table.
     * It thenLoad schedules the clearing of the selection to be performed on the Event Dispatch Thread (EDT)
     * using {@link SwingUtilities#invokeLater(Runnable)} to ensure thread safety in Swing applications.</p>
     *
     * @return A list of indices representing the currently selected rows in the table.
     * @see SwingUtilities#invokeLater(Runnable)
     */
    public List<Integer> getAndClearSelectedRows() {
        List<Integer> list = getSelectedRows();
        SwingUtilities.invokeLater(galleryTable::clearSelection);
        return list;
    }

    /**
     * Retrieves a list of selected row indices in the gallery table, converted to model indices.
     * <p>
     * This method returns the indices of the rows currently selected in the gallery table, but converted
     * from the view index (the index as displayed in the table) to the model index (the index in the data model).
     * The conversion is done using the provided {@code TableRowSorter}.
     * </p>
     *
     * @return a {@code List} of integers representing the selected row indices in the model
     */
    public List<Integer> getSelectedRows() {
        return Arrays.stream(galleryTable.getSelectedRows())
                .boxed()
                .map(galleryRowSorter::convertRowIndexToModel)
                .toList();
    }

    /**
     * Checks if any images are selected in the gallery table.
     * @return {@code true} if no images are selected, {@code false} otherwise.
     */
    public boolean ifAnySelected() {
        return !getSelectedRows().isEmpty();
    }

    /**
     * Configures the {@link FileChooser} for this view, setting up the dialog title,
     * the button that triggers the file chooser, and the strategy for processing file selections.
     * <p>
     * This method sets up the {@link FileChooser} thenLoad the specified {@link Controller} that
     * will handle the file selection results. The file chooser is configured to allow selection
     * of both files and directories.
     * </p>
     *
     * @param gc The {@link GalleryCoordinator} that will process the file selection results.
     *                   Must not be {@code null}.
     *
     * @throws NullPointerException if the {@code controller} is {@code null}.
     */
    public void setFileChooser(GalleryCoordinator gc) {
        Objects.requireNonNull(gc);

        fileChooser = new FileChooser<>(
                "view.gallery.file_chooser.dialog.title",
                openButton,
                gc::handleAddImages,
                new MultipleFileSelectionStrategy()
        );
        fileChooser.getFileChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    }

    /**
     * Sets the {@code TableRowSorter} for the gallery table and applies it to the table.
     * <p>
     * This method configures the table sorter used to sort and filter the gallery table rows.
     * It updates the table to use the provided {@code TableRowSorter} for managing the data view.
     * </p>
     *
     * @param galleryRowSorter the {@code TableRowSorter} to set for the gallery table
     *
     * @throws NullPointerException if {@code galleryRowSorter} is {@code null}
     */
    public void setGalleryRowSorter(TableRowSorter<GalleryTableModel> galleryRowSorter) {
        this.galleryRowSorter = galleryRowSorter;

        galleryTable.setRowSorter(galleryRowSorter);
    }

    /**
     * Disables all buttons in the gallery view.
     */
    public void lockModule() {
        for (JButton button : buttons) button.setEnabled(false);
    }

    /**
     * Enables all buttons in the gallery view.
     */
    public void unlockModule() {
        for (JButton button : buttons) button.setEnabled(true);
    }

    /**
     * Applies a filter to the gallery table based on the specified filter string.
     * <p>
     * This method creates a {@code RowFilter} that uses a regular expression to match rows
     * in the gallery table. The filter is applied to the table's data model to show only rows
     * that match the specified filter criteria.
     * </p>
     *
     * @param filter the filter string to apply to the gallery table
     *
     * @throws NullPointerException if {@code filter} is {@code null}
     */
    public void filterTable(String filter) {
        filter = Pattern.quote(filter);
        RowFilter<GalleryTableModel, Integer> rowFilter = RowFilter.regexFilter(
            ".*"+filter+".*", 0
        );
        galleryRowSorter.setRowFilter(rowFilter);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("comparer-processing")) {
            SwingUtilities.invokeLater(() -> {
                boolean val = (boolean) evt.getNewValue();
                if (val) lockModule();
                else unlockModule();
            });
        }
    }

    /**
     * The {@code Factory} class is a static factory for creating instances of {@link GalleryView}.
     * It provides methods for constructing and configuring all necessary parts.
     */
    public static class Factory {
        private static final Insets buttonInsets = new Insets(5, 10, 5, 10);

        /**
         * Creates a new {@link GalleryView} instance thenLoad default settings.
         *
         * @return A new instance of {@code GalleryView}.
         */
        public static GalleryView create() {
            JTable galleryTable = createTable();
            JLabel elementCountLabel = createCountLabel();
            JTextField nameFilterTextField = createFilterTextField();
            JButton addImageButton = createButton("view.gallery.button.image.add");
            JButton removeImageButton = createButton("view.gallery.button.image.remove");
            JButton deleteImageButton = createButton("view.gallery.button.image.delete");
            JButton distinctButton = createButton("view.gallery.button.distinct");
            JButton unifyNamesButton = createButton("view.gallery.button.unify_name");
            JButton openButton = createButton("view.gallery.button.image.open");
            JButton addTagButton = createButton("view.gallery.button.tag.add");
            JButton removeTagButton = createButton("view.gallery.button.tag.remove");

            return new GalleryView(
                galleryTable,
                elementCountLabel,
                nameFilterTextField,
                addImageButton,
                removeImageButton,
                deleteImageButton,
                distinctButton,
                unifyNamesButton,
                openButton,
                addTagButton,
                removeTagButton
            );
        }

        /**
         * Creates and configures the table for displaying gallery images.
         *
         * @return A new instance of {@link JTable}.
         */
        private static JTable createTable() {
            JTable table = new JTable();
            table.getTableHeader().setReorderingAllowed(false);

            table.addMouseListener(new MouseAdapter() {

                private static final int DOUBLE_CLICK_THRESHOLD = 500;
                private long lastClickTime = 0;

                @Override
                public void mouseClicked(MouseEvent e) {
                    long curr = System.currentTimeMillis();

                    if (curr - lastClickTime <= DOUBLE_CLICK_THRESHOLD) {
                        int r = table.rowAtPoint(e.getPoint());
                        int c = table.columnAtPoint(e.getPoint());

                        if (table.isCellEditable(r, c)) {
                            table.editCellAt(r, c);
                            Component editor = table.getEditorComponent();
                            editor.requestFocus();
                        }
                    }

                    lastClickTime = curr;
                }
            });

            return table;
        }

        /**
         * Creates and configures the label for displaying the count of elements in the gallery.
         *
         * @return A new instance of {@link JLabel}.
         */
        private static JLabel createCountLabel() {
            JLabel label = new JLabel("0");
            label.setFont(ComponentUtils.fontSmallHelveticaBold);

            return label;
        }

        /**
         * Creates and configures the text field for filtering images by name.
         *
         * @return A new instance of {@link JTextField}.
         */
        private static JTextField createFilterTextField() {
            JTextField textField = new JTextField();
            textField.setFont(ComponentUtils.fontHelveticaPlain);
            textField.setPreferredSize(new Dimension(150, 30));

            return textField;
        }

        /**
         * Creates and configures a button thenLoad the specified title.
         *
         * @param title The title of the button.
         * @return A new instance of {@link JButton}.
         */
        private static JButton createButton(String title) {
            return ComponentUtils.buttonFactory(title, buttonInsets);
        }
    }
}
