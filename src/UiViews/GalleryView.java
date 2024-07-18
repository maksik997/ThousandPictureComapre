package UiViews;

import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GalleryView extends AbstractView {

    private final JTable galleryTable;

    private final JButton addImageButton, removeImageButton, deleteImageButton, distinctButton, unifyNamesButton, openButton, addTagButton, removeTagButton;

    private final JButton[] buttons;

    private final JTextField nameFilterTextField;

    private final JFileChooser fileChooser;

    private final JLabel elementCount;

    public GalleryView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 2, 5, 2);

        JLabel headerLabel = new JLabel("LOC_GALLERY_VIEW_HEADER_LABEL");
        headerLabel.setFont(Utility.fontHelveticaBold);
        headerPanel.add(headerLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        headerPanel.add(Box.createHorizontalGlue(), c);
        c.gridx = 2;
        c.weightx = 0;

        JLabel nameFilterLabel = new JLabel("LOC_GALLERY_VIEW_NAME_FILTER_LABEL");
        nameFilterLabel.setFont(Utility.fontHelveticaPlain);
        headerPanel.add(nameFilterLabel, c);
        c.gridx = 3;

        nameFilterTextField = new JTextField();
        nameFilterTextField.setFont(Utility.fontHelveticaPlain);
        nameFilterTextField.setPreferredSize(new Dimension(150, 30));
        headerPanel.add(nameFilterTextField, c);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weighty = 0;

        buttonPanel.setBorder(new MatteBorder(0, 1, 0, 0, Color.GRAY));

        addImageButton = Utility.buttonFactory("LOC_GALLERY_VIEW_ADD_IMAGE_BUTTON", new Insets(5, 10, 5, 10));
        removeImageButton = Utility.buttonFactory("LOC_GALLERY_VIEW_REMOVE_IMAGE_BUTTON", new Insets(5, 10, 5, 10));
        deleteImageButton = Utility.buttonFactory("LOC_GALLERY_VIEW_DELETE_IMAGE_BUTTON", new Insets(5, 10, 5, 10));
        distinctButton = Utility.buttonFactory("LOC_GALLERY_VIEW_DISTINCT_BUTTON", new Insets(5, 10, 5, 10));
        unifyNamesButton = Utility.buttonFactory("LOC_GALLERY_VIEW_UNIFY_NAMES_BUTTON", new Insets(5, 10, 5, 10));
        openButton = Utility.buttonFactory("LOC_GALLERY_VIEW_OPEN_IMAGE_BUTTON", new Insets(5, 10, 5, 10));
        addTagButton = Utility.buttonFactory("LOC_GALLERY_VIEW_ADD_TAG_BUTTON", new Insets(5, 10, 5, 10));
        removeTagButton = Utility.buttonFactory("LOC_GALLERY_VIEW_REMOVE_TAG_BUTTON", new Insets(5, 10, 5, 10));

        buttonPanel.add(addImageButton, gbc);
        gbc.gridy++;
        buttonPanel.add(removeImageButton, gbc);
        gbc.gridy++;
        buttonPanel.add(deleteImageButton, gbc);
        gbc.gridy++;
        buttonPanel.add(distinctButton, gbc);
        gbc.gridy++;
        buttonPanel.add(unifyNamesButton, gbc);
        gbc.gridy++;
        buttonPanel.add(openButton, gbc);
        gbc.gridy++;
        buttonPanel.add(addTagButton, gbc);
        gbc.gridy++;
        buttonPanel.add(removeTagButton, gbc);
        gbc.gridy++;
        gbc.weighty = 1;
        buttonPanel.add(Box.createVerticalGlue(), gbc);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        galleryTable = new JTable();
        galleryTable.getTableHeader().setReorderingAllowed(false);

        tablePanel.add(new JScrollPane(galleryTable));

        JPanel elementCountPanel = new JPanel();
        elementCountPanel.setLayout(new BoxLayout(elementCountPanel, BoxLayout.X_AXIS));

        JLabel elementCountLabel = new JLabel("LOC_GALERY_ELEMENT_COUNT_LABEL");
        elementCountLabel.setFont(Utility.fontSmallHelveticaBold);
        elementCountLabel.setBorder(new EmptyBorder(0,5,0,5));

        elementCountPanel.add(elementCountLabel);

        elementCount = new JLabel("0");
        elementCount.setFont(Utility.fontSmallHelveticaBold);

        elementCountPanel.add(elementCount);
        elementCountPanel.add(Box.createHorizontalGlue());

        tablePanel.add(elementCountPanel);

        mainPanel.add(tablePanel);
        this.add(mainPanel);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("LOC_GALLERY_VIEW_FILE_CHOOSER_DIALOG_TITLE");
        fileChooser.setApproveButtonText("LOC_GALLERY_VIEW_FILE_CHOOSER_APPROVE_BUTTON_TEXT");

        buttons = new JButton[] {
            addImageButton,
            removeImageButton,
            deleteImageButton,
            distinctButton,
            unifyNamesButton,
            openButton,
            addTagButton,
            removeTagButton
        };
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

    public JLabel getElementCount() {
        return elementCount;
    }

    public JTextField getNameFilterTextField() {
        return nameFilterTextField;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public List<String> openFileChooser() {
        // Null if nothing selected
        // String with a path if something selected

        int file = fileChooser.showOpenDialog(this);
        if (file == JFileChooser.APPROVE_OPTION) {
            return Arrays.stream(fileChooser.getSelectedFiles()).map(File::toString).toList();
        }

        return null;
    }

    public void lockModule() {
        for (JButton button : buttons) button.setEnabled(false);
    }

    public void unlockModule() {
        for (JButton button : buttons) button.setEnabled(true);
    }
}
