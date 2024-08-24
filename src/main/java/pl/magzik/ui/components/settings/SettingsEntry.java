package pl.magzik.ui.components.settings;

import pl.magzik.ui.components.Utility;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Abstract class representing settings entry.
 * Have two fields {@link JLabel} and generic {@code T extends JComponent}, which holds value.
 * Supports {@link PropertyChangeSupport}.
 * @param <T> Type of value component must extend {@link JComponent}
 * @param <V> Type of returned value by using {@link #getValue()} and {@link #setValueProperty(Object)} methods.
 * */
public abstract class SettingsEntry<T extends JComponent, V> {
    private static final Border labelBorder = new CompoundBorder(
        new MatteBorder(0, 0, 1, 1, Color.GRAY),
        new EmptyBorder(10, 5, 10, 5)
    );

    private final JLabel label;
    protected final T value;
    private final PropertyChangeSupport pcs;
    protected V oldValue;

    /**
     * @param label {@link String} title of a label component.
     * @param value {@link T extends JComponent} component to be used as value component.
     * */
    public SettingsEntry(String label, T value) {
        this.label = new JLabel(label);
        this.label.setFont(Utility.fontHelveticaPlain);
        this.label.setBorder(labelBorder);

        this.value = value;

        oldValue = null;
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Returns label component
     * @return {@link JLabel} label component
     * */
    public JLabel getLabelComponent() {
        return label;
    }

    /**
     * Returns value component
     * @return {@link T extends JComponent} value component
     * */
    public T getValueComponent() {
        return value;
    }

    /**
     * Abstract method. Returns string value of a value component.
     * @return {@link V} value of value component.
     * */
    public abstract V getValue();

    /**
     * Returns value before change. Used for Item Listener
     * */
    protected V getOldValue() {
        return oldValue;
    }

    /**
     * Abstract method. Sets value of a value component.
     * Used by {@link #setValue(V)} method
     * @param value Takes {@link V} and sets it in a value component.
     * */
    protected abstract void setValueProperty(V value);

    /**
     * Returns value and notifies all the listeners that property has been changed.
     * @param value {@link V} value to be set.
     * */
    public void setValue(V value) {
        oldValue = value; // due to way of handling stock Swing component property change.
        setValueProperty(value);
        pcs.firePropertyChange("value", oldValue, value);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, V oldValue, V newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }
}
