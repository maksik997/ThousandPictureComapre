package UiViews;

import UiComponents.UiGalleryButtonPanel;
import UiComponents.UiHeader;
import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.List;

public class GalleryView extends AbstractView {

    /*private final UiGalleryButtonPanel uiGalleryButtonPanel;

    private final JTable filesTable;

    private DefaultTableModel model;

    private final JFileChooser fileChooser;*/

    private final JTable galleryTable;

    private final JButton addImageButton, removeImageButton, distinctButton, unifyNamesButton;

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

        JLabel headerLabel = new JLabel("Gallery:");
        headerLabel.setFont(Utility.fontHelveticaBold);
        headerLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));

        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
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

        buttonPanel.add(addImageButton, gbc);
        gbc.gridy++;
        buttonPanel.add(removeImageButton, gbc);
        gbc.gridy++;
        buttonPanel.add(distinctButton, gbc);
        gbc.gridy++;
        buttonPanel.add(unifyNamesButton, gbc);
        gbc.gridy++;
        gbc.weighty = 1;
        buttonPanel.add(Box.createVerticalGlue(), gbc);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        galleryTable = new JTable();

        mainPanel.add(new JScrollPane(galleryTable));

        this.add(mainPanel);
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
