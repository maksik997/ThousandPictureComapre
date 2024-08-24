package pl.magzik.UiComponents.Settings;

import pl.magzik.UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Implementation of {@link UiSettingsEntry} for checkboxes ({@link JCheckBox})
 * */
public class UiCheckBoxSettingsEntry extends UiSettingsEntry<JPanel, Boolean> {
    private static final Border panelBorder = new CompoundBorder(
        new MatteBorder(0, 0, 1, 0, Color.GRAY),
        new EmptyBorder(3, 5, 3, 5)
    );

    private JCheckBox checkBox;

    /**
     * @param label {@link String} title of a label component.
     * @param value {@link JPanel} panel to be used as a value component
     *                           (Panel must contain only {@link JCheckBox}).
     * @throws NullPointerException if given panel doesn't contain {@link JCheckBox}.
     * */
    public UiCheckBoxSettingsEntry(String label, JPanel value) {
        super(label, value);

        value.setBorder(panelBorder);

        // Searching for checkbox reference
        for (Component component : value.getComponents()) {
            if (component instanceof JCheckBox cb) checkBox = cb;
            if (checkBox != null) break;
        }

        if (checkBox == null)
            throw new NullPointerException("Checkbox is null. Bad panel.");

        checkBox.setFont(Utility.fontHelveticaPlain);

        checkBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Boolean oldVal = getOldValue();
                Boolean newVal = ((JCheckBox) e.getItem()).isSelected();
                firePropertyChange("value", oldVal, newVal);
            }
        });
    }

    @Override
    public Boolean getValue() {
        return checkBox.isSelected();
    }

    @Override
    protected void setValueProperty(Boolean value) {
        checkBox.setSelected(value);
    }
}
