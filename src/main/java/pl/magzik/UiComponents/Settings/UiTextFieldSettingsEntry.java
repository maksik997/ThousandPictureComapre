package pl.magzik.UiComponents.Settings;

import pl.magzik.UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;


/**
 * Implementation of {@link UiSettingsEntry} for text fields ({@link JTextField})
 * */
public class UiTextFieldSettingsEntry extends UiSettingsEntry<JPanel, String> {
    private static final Border panelBorder = new CompoundBorder(
        new MatteBorder(0, 0, 1, 0, Color.GRAY),
        new EmptyBorder(5, 5, 5, 5)
    ),
    textFieldBorder = new CompoundBorder(
            new LineBorder(Color.GRAY, 1, true),
            new EmptyBorder(4, 5, 4, 5)
    );

    private JTextField textField;

    /**
     * @param label {@link String} title of a label component.
     * @param value {@link JPanel} panel to be used as a value component
     *                           (Panel must contain only {@link JTextField}).
     * @throws NullPointerException if given panel doesn't contain {@link JTextField}.
     * */
    public UiTextFieldSettingsEntry(String label, JPanel value) {
        super(label, value);

        value.setBorder(panelBorder);
        value.setLayout(new GridLayout(1,1));

        // Searching for checkbox reference
        for (Component component : value.getComponents()) {
            if (component instanceof JTextField txt) textField = txt;
            if (textField != null) break;
        }

        if (textField == null)
            throw new NullPointerException("Checkbox is null. Bad panel.");

        textField.setEditable(true);
        textField.setFocusable(true);
        textField.setFont(Utility.fontHelveticaPlain);
        textField.setBorder(textFieldBorder);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateValue();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateValue();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateValue();
            }

            private void updateValue() {
                String oldVal = getOldValue();
                String newVal = textField.getText();
                firePropertyChange("value", oldVal, newVal);
            }
        });
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    protected void setValueProperty(String value) {
        textField.setText(value);
    }
}
