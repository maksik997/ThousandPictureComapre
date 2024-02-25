package UiViews;

import UiComponents.UiHeader;
import UiComponents.UiSettings;
import UiComponents.Utility;
import pl.magzik.Comparer;

import javax.swing.*;
import java.awt.*;

public class SettingsView extends JPanel {

    private final UiSettings uiSettings_;

    private final JButton backButton;


    public SettingsView() {
        this.setLayout(new BorderLayout());

        // update v0.3
        UiHeader uiHeader_ = new UiHeader();
        this.uiSettings_ = new UiSettings();
        this.backButton = Utility.buttonFactory("Back", new Insets(5, 15, 5, 15));

        uiHeader_.getSettingsButton().setVisible(false);

        this.add(uiHeader_, BorderLayout.PAGE_START);
        this.add(uiSettings_);
        this.add(backButton, BorderLayout.PAGE_END);
    }

    public JButton getBackButton() {
        return backButton;
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
