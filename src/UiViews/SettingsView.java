package UiViews;

import UiComponents.UiHeader;
import UiComponents.UiSettings;
import UiComponents.Utility;
import pl.magzik.Comparer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsView extends AbstractView {

    private final UiSettings uiSettings_;


    public SettingsView() {
        super.uiHeader_.toggleButton(UiHeader.Button.SETTINGS);

        this.uiSettings_ = new UiSettings();

        this.add(uiSettings_);
    }

    // Couple of easy access methods :)
    public String getPath() {
        return uiSettings_.getPath();
    }

    public boolean openFileChooser() {
        return uiSettings_.openFileChooser();
    }

    public JButton getPathButton() {
        return uiSettings_.getPathButton();
    }

    public JComboBox<Comparer.Modes> getModeSelector() {
        return uiSettings_.getModeComboBox();
    }
}
