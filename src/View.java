import UiComponents.UiHeader;
import UiViews.AbstractView;
import UiViews.GalleryView;
import UiViews.LocationView;
import UiViews.SettingsView;
import UiComponents.Utility;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class View extends JFrame {

    // Different Panels
    private final LocationView locationView;
    private final SettingsView settingsView;
    private final GalleryView galleryView;

    // UPDATE 0.4
    private final List<AbstractView> scenes;


    public View() throws HeadlessException, IOException {
        scenes = new ArrayList<>();

        galleryView = new GalleryView();
        settingsView = new SettingsView();
        locationView = new LocationView();

        scenes.add(galleryView);
        scenes.add(settingsView);
        scenes.add(locationView);

        ImageIcon icon = new ImageIcon("resources/thumbnail.png");

        this.add(locationView);

        this.setTitle("Thousand Picture Comapre v0.3.3");
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

    public GalleryView getGalleryView() {
        return galleryView;
    }

    public List<AbstractView> getScenes() {
        return List.copyOf(scenes);
    }

    public void toggleScene(Utility.Scene scene) {
        switch (scene) {
            case SETTINGS -> {
                remove(locationView);
                remove(galleryView);
                add(settingsView);
            }
            case COMPARER -> {
                remove(settingsView);
                remove(galleryView);
                add(locationView);
            }
            case GALLERY -> {
                remove(locationView);
                remove(settingsView);
                add(galleryView);
            }
        }
        
        repaint();
        revalidate();
    }
}
