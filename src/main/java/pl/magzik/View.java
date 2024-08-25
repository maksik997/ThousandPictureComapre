package pl.magzik;

import com.formdev.flatlaf.util.SystemInfo;
import pl.magzik.ui.components.Utility;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class View extends JFrame implements MessageInterface {

    // Different Panels
    private final ComparerView comparerView;
    private final SettingsView settingsView;
    private final GalleryView galleryView;
    private final MenuView menuView;
    private final CreditsView creditsView;

    private final List<JPanel> scenes;

    public View() throws HeadlessException {
        scenes = new ArrayList<>();

        menuView = new MenuView();
        galleryView = new GalleryView();
        settingsView = SettingsView.Factory.create();
        comparerView = new ComparerView();
        creditsView = new CreditsView();

        scenes.add(galleryView);
        scenes.add(settingsView);
        scenes.add(comparerView);
        scenes.add(menuView);
        scenes.add(creditsView);

        ImageIcon icon = new ImageIcon("data/thumbnail_64x64.png");

        this.add(menuView);

        if (SystemInfo.isMacOS) {
            getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
        }
        this.setTitle("general.title");
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

    @Override
    public void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(
            this,
            String.format(message),
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showErrorMessage(String message, String title, Exception e) {
        JOptionPane.showMessageDialog(
            this,
            String.format(message, e.getMessage()),
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void showInformationMessage(String message, String title) {
        JOptionPane.showMessageDialog(
            this,
            String.format(message),
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}