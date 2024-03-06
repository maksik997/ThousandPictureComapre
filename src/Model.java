import Modules.ComparerModule;
import Modules.GalleryModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Model {

    private final ComparerModule comparerModule;

    private final GalleryModule galleryModule;

    public Model() throws IOException {
        init();

        comparerModule = new ComparerModule();
        galleryModule = new GalleryModule();
    }

    public ComparerModule getComparerModule() {
        return comparerModule;
    }

    private void init() throws IOException {
        // This method will check if meta files were created,
        // and if not will create them.

        Path metaDir = Path.of(System.getProperty("user.dir") + File.separator + "meta");
        if(!Files.exists(metaDir)) {
            Files.createDirectory(metaDir);
        }

    }
}
