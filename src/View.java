// todo
//  Add loading frame for model,

import UiComponents.Utility;
import UiViews.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class View extends JFrame {

    // Different Panels
    private final ComparerView comparerView;
    private final SettingsView settingsView;
    private final GalleryView galleryView;
    private final MenuView menuView;
    private final CreditsView creditsView;

    private final List<JPanel> scenes;

    public View() throws HeadlessException, IOException {
        scenes = new ArrayList<>();

        menuView = new MenuView();
        galleryView = new GalleryView();
        settingsView = new SettingsView();
        comparerView = new ComparerView();
        creditsView = new CreditsView();

        scenes.add(galleryView);
        scenes.add(settingsView);
        scenes.add(comparerView);
        scenes.add(menuView);
        scenes.add(creditsView);

        ImageIcon icon = new ImageIcon("resources/thumbnail.png");

        this.add(menuView);

        this.setTitle("Thousand Picture Comapre`");
        this.setIconImage(icon.getImage());
        this.setMinimumSize(new Dimension(800, 650));
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public ComparerView getComparerView() {
        return comparerView;
    }

    public SettingsView getSettingsView() {
        return settingsView;
    }

    public GalleryView getGalleryView() {
        return galleryView;
    }

    public MenuView getMenuView() {
        return menuView;
    }

    public List<JPanel> getScenes() {
        return List.copyOf(scenes);
    }

    public void toggleScene(Utility.Scene scene) {
        scenes.forEach(this::remove);

        switch (scene) {
            case SETTINGS -> add(settingsView);
            case COMPARER -> add(comparerView);
            case GALLERY -> add(galleryView);
            case MENU -> add(menuView);
            case CREDITS -> add(creditsView);
        }
        
        repaint();
        revalidate();
    }
}
