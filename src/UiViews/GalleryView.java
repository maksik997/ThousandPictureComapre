package UiViews;

import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;

public class GalleryView extends AbstractView {

    /*private final UiGalleryButtonPanel uiGalleryButtonPanel;

    private final JTable filesTable;

    private DefaultTableModel model;

    private final JFileChooser fileChooser;*/

    private final JTable galleryTable;

    private final JTextField searchForImage;

    private final JButton addImageButton, removeImageButton, distinctButton, unifyNamesButton, searchButton, openButton;

    private final JFileChooser fileChooser;

    public GalleryView() {
//        super.uiHeader_.toggleButton(UiHeader.Button.GALLERY);
//        uiGalleryButtonPanel = new UiGalleryButtonPanel();
//
//        fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        fileChooser.setMultiSelectionEnabled(true);
//
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new BorderLayout());
//
//        filesTable = new JTable(model);
//        filesTable.setFont(Utility.fontHelveticaPlain);
//        filesTable.getTableHeader().setFont(Utility.fontHelveticaBold);
//
//        updateTableModel(null);
//
//
//        mainPanel.add(uiGalleryButtonPanel, BorderLayout.NORTH);
//        mainPanel.add(new JScrollPane(filesTable));
//
//        this.add(mainPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;

        headerPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            new EmptyBorder(5, 0, 5, 0)
        ));

        JLabel headerLabel = new JLabel("Gallery:");
        headerLabel.setFont(Utility.fontHelveticaBold);

        headerPanel.add(headerLabel, gbc);
        gbc.gridx++;
        gbc.weightx = 1;
        headerPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5,5, 5, 0);

        searchForImage = new JTextField(25);
        searchForImage.setFont(Utility.fontHelveticaPlain);

        headerPanel.add(searchForImage, gbc);
        gbc.gridx++;
        gbc.insets = new Insets(5,0, 5, 5);

        searchButton = Utility.buttonFactory("Search", new Insets(5, 10, 5, 10));
        headerPanel.add(searchButton, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weighty = 0;

        buttonPanel.setBorder(new MatteBorder(0, 1, 0, 0, Color.GRAY));

        addImageButton = Utility.buttonFactory("Add", new Insets(5, 10, 5, 10));
        removeImageButton = Utility.buttonFactory("Remove", new Insets(5, 10, 5, 10));
        distinctButton = Utility.buttonFactory("Distinct", new Insets(5, 10, 5, 10));
        unifyNamesButton = Utility.buttonFactory("Unify", new Insets(5, 10, 5, 10));
        openButton = Utility.buttonFactory("Open", new Insets(5, 10, 5, 10));

        buttonPanel.add(addImageButton, gbc);
        gbc.gridy++;
        buttonPanel.add(removeImageButton, gbc);
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
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Pick image you want to add:");
        fileChooser.setApproveButtonText("Pick");
    }

    public JButton getAddImageButton() {
        return addImageButton;
    }

    public JButton getRemoveImageButton() {
        return removeImageButton;
    }

    public JButton getDistinctButton() {
        return distinctButton;
    }

    public JButton getUnifyNamesButton() {
        return unifyNamesButton;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getOpenButton() {
        return openButton;
    }

    public JTable getGalleryTable() {
        return galleryTable;
    }

    public JTextField getSearchForImage() {
        return searchForImage;
    }

    public String openFileChooser() {
        // Null if nothing selected
        // String with a path if something selected
        int file = fileChooser.showOpenDialog(this);
        if (file == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }

        return null;
    }

    /*    public UiGalleryButtonPanel getUiGalleryButtonPanel() {
        return uiGalleryButtonPanel;
    }

    public JTable getFilesTable() {
        return filesTable;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public File[] openFileChooser() {
        int file = fileChooser.showOpenDialog(this);
        if(file == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFiles();
        }
        return null;
    }

    public String openInputDialog(String msg, String title, String[] data, String base) {
        return (String) JOptionPane.showInputDialog(
                null, msg,
                title, JOptionPane.PLAIN_MESSAGE,
                null, data, base
        );
    }

    public void updateTableModel(List<List<Object>> data) {
        model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("File");
        model.addColumn("Tags");
        filesTable.setRowSorter(new TableRowSorter<>(model));


        filesTable.setModel(model);
        filesTable.getColumnModel().getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            File f = (File) value;
            return filesTable.getDefaultRenderer(Object.class)
                    .getTableCellRendererComponent(table, f.getName(), isSelected, hasFocus, row, column);
        });

        if (data == null)
            return;

        for (int i = 0; i < data.get(0).size(); i++) {
            model.addRow(
                new Object[]{
                    data.get(0).get(i),
                    data.get(1).get(i)
                }
            );
        }
    }*/
}
