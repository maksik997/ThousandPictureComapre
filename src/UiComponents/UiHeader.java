package UiComponents;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class UiHeader extends JPanel {

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
        this.settingsButton = Utility.buttonFactory("Settings", new Insets(5, 15, 5, 15));

        title.setFont(Utility.fontHelveticaBold);

        this.add(title, BorderLayout.LINE_START);
        this.add(settingsButton, BorderLayout.LINE_END);
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }
}
