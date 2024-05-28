package UiComponents;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class UiHeader extends JPanel {

    private final JButton backButton;

    public UiHeader() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(
            new CompoundBorder(
                new MatteBorder(0,0,1,0, Color.GRAY),
                new EmptyBorder(0, 10, 10, 10)
            )
        );

        ImageIcon thumbnail = Utility.getScaledImage(new ImageIcon("resources/thumbnail.png"), 50, 50);

        JLabel title = new JLabel(
            "Thousand Picture Comapre`",
            thumbnail,
            JLabel.LEFT
        );
        title.setFont(Utility.fontHelveticaBold);

        backButton = Utility.buttonFactory(
            "Back",
            new Insets(5, 15, 5, 15)
        );

        this.add(title);
        this.add(Box.createHorizontalGlue());
        this.add(backButton);
    }

    public JButton getBackButton() {
        return backButton;
    }

}
