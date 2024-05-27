//package UiComponents;
//
//import pl.magzik.Comparer;
//
//import javax.swing.*;
//import javax.swing.border.LineBorder;
//import javax.swing.border.TitledBorder;
//import javax.swing.plaf.basic.BasicComboBoxRenderer;
//import java.awt.*;
//
//public class UiSettings extends JPanel {
//
//    private final UiPath uiPath_;
//
//    private final JComboBox<Comparer.Modes> modeComboBox;
//
//    public UiSettings() {
//        this.setLayout(new GridBagLayout());
//        this.setBorder(new TitledBorder(
//            new LineBorder(Color.GRAY, 2),
//            "Settings",
//            TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
//            Utility.fontBigHelveticaBold
//        ));
//
//        this.uiPath_ = new UiPath();
//        this.modeComboBox = new JComboBox<>(Comparer.Modes.values());
//
//        this.uiPath_.setBorder(new TitledBorder(
//            new LineBorder(Color.GRAY, 2),
//            "Destination directory:",
//            TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
//            Utility.fontSmallHelveticaBold
//        ));
//
//        this.modeComboBox.setRenderer(new UiComboBoxRenderer<>());
//        this.modeComboBox.setBorder(new TitledBorder(
//            new LineBorder(Color.GRAY, 2),
//            "Comparing mode selection:",
//            TitledBorder.CENTER, TitledBorder.ABOVE_TOP,
//            Utility.fontSmallHelveticaBold
//        ));
//        this.modeComboBox.setSelectedItem(Comparer.Modes.NOT_RECURSIVE);
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.anchor = GridBagConstraints.NORTH;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.weightx = 1.d;
//        gbc.gridy = 0;
//
//        this.add(uiPath_, gbc);
//
//        gbc.weighty = 1.d;
//        gbc.gridy++;
//
//        this.add(modeComboBox, gbc);
//    }
//
//    public JComboBox<Comparer.Modes> getModeComboBox() {
//        return modeComboBox;
//    }
//
//    // Couple of easy access methods :)
//    public String getPath() {
//        return uiPath_.getPath();
//    }
//
//    public boolean openFileChooser() {
//        return uiPath_.openFileChooser();
//    }
//
//    public JButton getPathButton() {
//        return uiPath_.getPathButton();
//    }
//}
