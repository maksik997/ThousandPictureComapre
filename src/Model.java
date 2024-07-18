import Modules.ComparerModule;
import Modules.GalleryModule;
import Modules.SettingsModule;

import java.io.IOException;

public class Model {

    private static final String configPath = "./data/config.cfg";

    private final ComparerModule comparerModule;

    private final GalleryModule galleryModule;

    private final SettingsModule settingsModule;

    public Model() throws IOException {
        this.settingsModule = new SettingsModule(configPath);
        this.settingsModule.loadSettings();

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

}
