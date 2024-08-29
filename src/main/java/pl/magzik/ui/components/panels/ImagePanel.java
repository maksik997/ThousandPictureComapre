package pl.magzik.ui.components.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * The {@code ImagePanel} class is a custom {@link JPanel} that displays an image.
 * <p>
 * The image is loaded from a file and is drawn on the panel when it is rendered.
 * </p>
 */
public class ImagePanel extends JPanel {
    private Image image;

    /**
     * Constructs an {@code ImagePanel} with the image loaded from the specified file.
     *
     * @param file the {@link File} object representing the image file to be loaded
     */
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
