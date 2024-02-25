package UiComponents;

import javax.swing.*;
import java.awt.*;

public class UiComboBoxRenderer<T> extends JLabel implements ListCellRenderer<T>{

    public UiComboBoxRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
        String selected = value.toString().replaceAll("_", " ").toLowerCase();
        selected = selected.replaceFirst(
                String.valueOf(selected.charAt(0)),
                String.valueOf((char) (selected.charAt(0) - 'a' + 'A'))
        );

        if(isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setText(selected);

        return this;
    }
}
