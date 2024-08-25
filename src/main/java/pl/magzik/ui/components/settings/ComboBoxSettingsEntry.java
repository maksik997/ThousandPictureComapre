package pl.magzik.ui.components.settings;

import pl.magzik.ui.components.Utility;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

/**
 * Implementation of {@link SettingsEntry} for {@link JComboBox<String>}
 * */
public class ComboBoxSettingsEntry extends SettingsEntry<JComboBox<String>, String> {
    private static final Border comboBoxBorder = new CompoundBorder(
        new MatteBorder(0, 0, 1, 0, Color.GRAY),
        new EmptyBorder(8, 5, 8, 5)
    );

    /**
     * @param label {@link String} title of a label component.
     * @param value {@link JComboBox} to be used as a value component
     * */
    public ComboBoxSettingsEntry(String label, JComboBox<String> value) {
        super(label, value);

        value.setFont(Utility.fontHelveticaPlain);
        value.setBorder(comboBoxBorder);

        value.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String oldVal = getOldValue();
                String newVal = e.getItem().toString();
                firePropertyChange(oldVal, newVal);
            }
        });
    }

    /**
     * Initializes value {@link JComboBox} with given {@link String} array.
     * @param values values to be inside {@link JComboBox}.
     * */
    public void initializeComboBox(String... values) {
        Arrays.stream(values).forEach(value::addItem);
    }

    @Override
    public String getValue() {
        return (String) value.getSelectedItem();
    }

    @Override
    protected void setValueProperty(String value) {
        this.value.setSelectedItem(value);
    }
}
