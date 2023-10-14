import MinorViews.LocationView;
import MinorViews.SettingsView;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    // Different Panels
    private final LocationView locationView;
    private final SettingsView settingsView;

    public View() throws HeadlessException {
        locationView = new LocationView();
        settingsView = new SettingsView();

        this.add(locationView);

        this.setTitle("Thousand Picture Redundancy");
        this.setSize(800, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        new Controller(this, new Model());
    }

    public LocationView getLocationView() {
        return locationView;
    }

    public SettingsView getSettingsView() {
        return settingsView;
    }
}
