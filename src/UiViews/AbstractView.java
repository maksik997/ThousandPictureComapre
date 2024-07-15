package UiViews;

import UiComponents.Utility;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public abstract class AbstractView extends JPanel {
    private final JButton backButton;

    public AbstractView() {
        this.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setBorder(
            new CompoundBorder(
                new MatteBorder(0,0,1,0, Color.GRAY),
                new EmptyBorder(0, 10, 10, 10)
            )
        );

        ImageIcon thumbnail = Utility.getScaledImage(new ImageIcon("resources/thumbnail.png"), 50, 50);
        JLabel title = new JLabel(
                "TITLE_TAG",
                thumbnail,
                JLabel.LEFT
        );
        title.setFont(Utility.fontHelveticaBold);

        backButton = Utility.buttonFactory(
                "LOC_ABSTRACT_VIEW_BACK_BUTTON",
                new Insets(5, 15, 5, 15)
        );

        header.add(title);
        header.add(Box.createHorizontalGlue());
        header.add(backButton);

        this.add(header, BorderLayout.NORTH);
    }

    public JButton getBackButton() {
        return backButton;
    }
}

