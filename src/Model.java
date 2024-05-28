import Modules.ComparerModule;
import Modules.GalleryModule;
import Modules.SettingsModule;

import java.io.IOException;

public class Model {

    private static final String configPath = "./resources/config.cfg";

    private final ComparerModule comparerModule;

    private final GalleryModule galleryModule;

    private final SettingsModule settingsModule;

    private boolean isLoaded;

    public Model() throws IOException {
        isLoaded = false;
        this.settingsModule = new SettingsModule(configPath);
        this.settingsModule.loadSettings();

        this.comparerModule = new ComparerModule();
        this.galleryModule = new GalleryModule();
        isLoaded = true;
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

    public boolean isLoaded() {
        return isLoaded;
    }
}
