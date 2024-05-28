package UiComponents;

import javax.swing.*;
public class UiTray extends JPanel {

    private final JTextField totalField, duplicatesField;

    public UiTray() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.totalField = Utility.constTextFieldFactory("Total:", "0", 6);
        this.duplicatesField = Utility.constTextFieldFactory("Duplicates: ", "0", 6);

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

    public JTextField getTotalField() {
        return totalField;
    }

    public JTextField getDuplicatesField() {
        return duplicatesField;
    }
}
