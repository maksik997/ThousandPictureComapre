import Modules.ComparerModule;
import Modules.GalleryModule;
import Modules.SettingsModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model {

    private static final String configPath = "./resources/config.cfg";

    private final ComparerModule comparerModule;

    private final GalleryModule galleryModule;

    private final SettingsModule settingsModule;

    public Model() throws IOException {
        this.settingsModule = new SettingsModule(configPath);
        init();

        this.comparerModule = new ComparerModule();
        this.galleryModule = new GalleryModule();
    }

    public ComparerModule getComparerModule() {
        return comparerModule;
    }

    public GalleryModule getGalleryModule() {
        return galleryModule;
    }

    public SettingsModule getSettingsModule() {
        return settingsModule;
    }

    private void init() throws IOException {
        this.settingsModule.loadSettings();
    }
}
