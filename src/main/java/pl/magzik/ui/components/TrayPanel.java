package pl.magzik.ui.components;

import javax.swing.*;
public class TrayPanel extends JPanel {

    private final JTextField totalField, duplicatesField;

    public TrayPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.totalField = Utility.constTextFieldFactory("view.comparer.tray.total.border.title", "0", 6);
        this.duplicatesField = Utility.constTextFieldFactory("view.comparer.tray.duplicates.border.title", "0", 6);

        this.add(totalField);
        this.add(Box.createHorizontalGlue());
        this.add(duplicatesField);
    }

    public void update(long total, long duplicates) {
        totalField.setText(String.valueOf(total));
        duplicatesField.setText(String.valueOf(duplicates));
    }

    public void clear() {
        update(0, 0);
    }

}