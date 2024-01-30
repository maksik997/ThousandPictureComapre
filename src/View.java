import UiViews.LocationView;
import UiViews.SettingsView;
import UiComponents.Utility;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {

    // Different Panels
    private final LocationView locationView;
    private final SettingsView settingsView;

    public View() throws HeadlessException {
        locationView = new LocationView();
        settingsView = new SettingsView();

        ImageIcon icon = new ImageIcon("resources/thumbnail.png");

        this.add(locationView);

        this.setTitle("Thousand Picture Comapre v0.3.2");
        this.setIconImage(icon.getImage());
        this.setMinimumSize(new Dimension(800, 400));
        this.pack();

        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        new Controller(this, new Model());
    }

    public LocationView getLocationView() {
        return locationView;
    }

    public SettingsView getSettingsView() {
        return settingsView;
    }

    public void toggleScene(Utility.Scene scene) {
        if (scene == Utility.Scene.MAIN) {
            remove(settingsView);
            add(locationView);
        } else {
            remove(locationView);
            add(settingsView);
        }
        repaint();
        revalidate();
    }
}
