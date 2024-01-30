package UiComponents;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Utility {

    public static Font fontHelveticaPlain = new Font("Helvetica", Font.PLAIN, 16),
                        fontHelveticaBold = new Font("Helvetica", Font.BOLD, 16);


    public static ImageIcon getScaledImage(ImageIcon imageIcon, int w, int h) {
        // Source: https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
        // With edit

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );
        g2.drawImage(imageIcon.getImage(), 0, 0, w, h, null);
        g2.dispose();

        return new ImageIcon(resizedImg);
    }

    public static JButton buttonFactory(String title, Insets insets) {
        JButton button = new JButton(title);
        button.setFocusable(false);
        button.setBorder(new EmptyBorder(insets));
        button.setFont(fontHelveticaPlain);
        return button;
    }
}
