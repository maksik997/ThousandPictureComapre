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

    private final JButton addImageButton, removeImageButton, deleteImageButton, distinctButton, unifyNamesButton, openButton;

    private final JButton[] buttons;

    private final JFileChooser fileChooser;

    public GalleryView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();

        headerPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JLabel headerLabel = new JLabel("LOC_GALLERY_VIEW_HEADER_LABEL");
        headerLabel.setFont(Utility.fontHelveticaBold);
        headerPanel.add(headerLabel);

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
        gbc.weighty = 1;
        buttonPanel.add(Box.createVerticalGlue(), gbc);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        galleryTable = new JTable();

        mainPanel.add(new JScrollPane(galleryTable));

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

    public JTable getGalleryTable() {
        return galleryTable;
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
