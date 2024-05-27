package UiComponents;

import javax.swing.*;
public class UiTray extends JPanel {

    private final JTextField totalField/*, processedField*/, duplicatesField;

    public UiTray() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.totalField = Utility.constTextFieldFactory("Total:", "0", 6);
//        this.processedField = Utility.constTextFieldFactory("Processed:", "0", 6);
        this.duplicatesField = Utility.constTextFieldFactory("Duplicates: ", "0", 6);

        this.add(totalField);
//        this.add(Box.createHorizontalGlue());
//        this.add(processedField);
        this.add(Box.createHorizontalGlue());
        this.add(duplicatesField);
    }

    public void update(long total/*, long processed*/, long duplicates) {
        totalField.setText(String.valueOf(total));
//        processedField.setText(String.valueOf(processed));
        duplicatesField.setText(String.valueOf(duplicates));
    }

    public void clear() {
        update(0,/* 0,*/ 0);
    }

    public JTextField getTotalField() {
        return totalField;
    }

//    public JTextField getProcessedField() {
//        return processedField;
//    }

    public JTextField getDuplicatesField() {
        return duplicatesField;
    }
}
