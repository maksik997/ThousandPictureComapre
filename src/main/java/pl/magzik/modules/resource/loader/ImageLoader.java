package pl.magzik.modules.resource.loader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Loads image resources from URLs.
 * Implements {@link ResourceLoader} to handle image resources.
 */
public class ImageLoader implements ResourceLoader<Image> {

    /**
     * Loads an image resource from the given file name.
     *
     * @param resource The name of the image resource to load.
     * @return The loaded {@link Image} object.
     * @throws IOException If an I/O error occurs during loading.
     */
    @Override
    public Image load(String resource) throws IOException {
        URL url = getResource(resource);
        return ImageIO.read(url);
    }
}
