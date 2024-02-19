package UiComponents;

import javax.swing.*;
public class UiTray extends JPanel {

    private final JTextField totalField_, processedField_, duplicatesField_;

    public UiTray() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.totalField_ = Utility.constTextFieldFactory("Total:", "0", 6);
        this.processedField_ = Utility.constTextFieldFactory("Processed:", "0", 6);
        this.duplicatesField_ = Utility.constTextFieldFactory("Duplicates: ", "0", 6);

        this.add(totalField_);
        this.add(processedField_);
        this.add(duplicatesField_);
    }

    public void update(long total, long processed, long duplicates) {
        totalField_.setText(String.valueOf(total));
        processedField_.setText(String.valueOf(processed));
        duplicatesField_.setText(String.valueOf(duplicates));
    }

    public void clear() {
        update(0, 0, 0);
    }

    public JTextField getTotalField_() {
        return totalField_;
    }

    public JTextField getProcessedField_() {
        return processedField_;
    }

    public JTextField getDuplicatesField_() {
        return duplicatesField_;
    }
}
