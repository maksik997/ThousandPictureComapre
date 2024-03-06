package UiComponents;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class UiHeader extends JPanel {

    private final JButton comparerButton;
    private final JButton galleryButton;
    private final JButton settingsButton;

    public UiHeader() {
        this.setLayout(new BorderLayout());
        this.setBorder(
            new CompoundBorder(
                new MatteBorder(0,0,1,0, Color.GRAY),
                new EmptyBorder(0, 10, 10, 10)
            )
        );

        ImageIcon thumbnail = Utility.getScaledImage(new ImageIcon("resources/thumbnail.png"), 50, 50);

        JLabel title = new JLabel("Thousand Picture Comapre", thumbnail, JLabel.LEFT);

        JPanel buttonOverlay = new JPanel();
        buttonOverlay.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        this.comparerButton = Utility.buttonFactory("Comparer", new Insets(5, 15, 5, 15));
        this.galleryButton = Utility.buttonFactory("Gallery", new Insets(5, 15, 5, 15));
        this.settingsButton = Utility.buttonFactory("Settings", new Insets(5, 15, 5, 15));

        buttonOverlay.add(comparerButton, gbc);
        gbc.gridy++;
        buttonOverlay.add(galleryButton, gbc);
        gbc.gridy++;
        buttonOverlay.add(settingsButton, gbc);

        title.setFont(Utility.fontHelveticaBold);

        this.add(title, BorderLayout.LINE_START);
        this.add(buttonOverlay, BorderLayout.LINE_END);
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getComparerButton() {
        return comparerButton;
    }

    public JButton getGalleryButton() {
        return galleryButton;
    }

    public enum Button {
        COMPARER, GALLERY, SETTINGS
    }

    public void toggleButton(Button button) {
        settingsButton.setEnabled(true);
        comparerButton.setEnabled(true);
        galleryButton.setEnabled(true);
        switch (button) {
            case COMPARER -> comparerButton.setEnabled(false);
            case GALLERY -> galleryButton.setEnabled(false);
            case SETTINGS -> settingsButton.setEnabled(false);
        }
    }
}
