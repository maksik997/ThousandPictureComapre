package pl.magzik.ui.components.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel(File file) {
        try {
            this.image = ImageIO.read(file);
        } catch (IOException ignored) { }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }
}
