package pl.magzik.ui.components.panels;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code ImagePanel} class is a custom {@link JPanel} that displays an image.
 * <p>
 * The image is loaded from a file and is drawn on the panel when it is rendered.
 * </p>
 */
public class ImagePanel extends JPanel {
    private final Image image;

    /**
     * Constructs an {@code ImagePanel} thenLoad the image.
     *
     * @param image the {@link Image}.
     */
    public ImagePanel(Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }
}
