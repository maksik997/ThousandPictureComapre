package pl.magzik.ui.components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Utility {

    public static Font fontHelveticaPlain = new Font("Helvetica", Font.PLAIN, 16),
                        fontHelveticaBold = new Font("Helvetica", Font.BOLD, 16),
                        fontSmallHelveticaBold = new Font("Helvetica", Font.BOLD, 12),
                        fontBigHelveticaBold = new Font("Helvetica", Font.BOLD, 32);


    public static ImageIcon getScaledImage(ImageIcon imageIcon, int w, int h) {
        // Source: https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
        // With edits

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

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

    public static JTextField constTextFieldFactory(String title, String initial, int cols) {
        JTextField textField = new JTextField(initial, cols);

        textField.setEditable(false);
        textField.setFocusable(false);
        textField.setFont(fontBigHelveticaBold);
        textField.setHorizontalAlignment(SwingConstants.CENTER);

        textField.setBorder(new TitledBorder(
            textField.getBorder(),
            title,
            TitledBorder.CENTER, TitledBorder.TOP,
            Utility.fontSmallHelveticaBold
        ));

        return textField;
    }

    public enum Scene {
        SETTINGS, COMPARER, GALLERY, MENU, CREDITS
    }
}
