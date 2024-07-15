package UiViews;

import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class MenuView extends JPanel {

    private final JButton comparerButton, galleryButton, settingsButton, creditsButton, exitButton;

    public MenuView() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;


        ImageIcon icon = Utility.getScaledImage(new ImageIcon("resources/thumbnail.png"), 200, 200);
        JLabel iconLabel = new JLabel(icon);

        JLabel title = new JLabel("TITLE_TAG");
        title.setFont(Utility.fontBigHelveticaBold);
        title.setBorder(
                new MatteBorder(0,0,1,0, Color.GRAY)
        );

        JLabel versionTag = new JLabel("VERSION_TAG");
        versionTag.setFont(Utility.fontSmallHelveticaBold);

        comparerButton = Utility.buttonFactory(
            "LOC_MENU_VIEW_COMPARER_BUTTON",
            new Insets(10, 15, 10, 15)
        );
        comparerButton.setPreferredSize(new Dimension(200, 50));

        galleryButton = Utility.buttonFactory(
                "LOC_MENU_VIEW_GALLERY_BUTTON",
                new Insets(10, 15, 10, 15)
        );
        galleryButton.setPreferredSize(new Dimension(200, 50));

        settingsButton = Utility.buttonFactory(
                "LOC_MENU_VIEW_SETTINGS_BUTTON",
                new Insets(10, 15, 10, 15)
        );
        settingsButton.setPreferredSize(new Dimension(200, 50));

        creditsButton = Utility.buttonFactory(
            "LOC_MENU_VIEW_CREDITS_BUTTON",
            new Insets(10, 15, 10, 15)
        );
        creditsButton.setPreferredSize(new Dimension(200, 50));

        exitButton = Utility.buttonFactory(
        "LOC_MENU_VIEW_EXIT_BUTTON",
            new Insets(10, 15, 10, 15)
        );
        exitButton.setPreferredSize(new Dimension(200, 50));

        gbc.gridy = 0;
        this.add(iconLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 0, 0);
        this.add(title, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        this.add(versionTag, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        this.add(comparerButton, gbc);
        gbc.gridy++;
        this.add(galleryButton, gbc);
        gbc.gridy++;
        this.add(settingsButton, gbc);
        gbc.gridy++;
        this.add(creditsButton, gbc);
        gbc.gridy++;
        this.add(exitButton, gbc);
    }

    public JButton getComparerButton() {
        return comparerButton;
    }

    public JButton getGalleryButton() {
        return galleryButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getCreditsButton() {
        return creditsButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }
}
