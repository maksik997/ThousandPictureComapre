package pl.magzik;

import pl.magzik.modules.ComparerModule;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.SettingsModule;

public class Model {
    private final ComparerModule comparerModule;
    private final GalleryModule galleryModule;
    private final SettingsModule settingsModule;

    public Model() {
        this.settingsModule = new SettingsModule();
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
