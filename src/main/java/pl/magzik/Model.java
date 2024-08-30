package pl.magzik;

import pl.magzik.modules.ComparerModule;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.SettingsModule;
import pl.magzik.modules.comparer.ComparerFileModule;
import pl.magzik.modules.comparer.ComparerPropertyAccess;

public class Model {
    private final ComparerModule comparerModule;
    private final ComparerFileModule comparerFileModule;
    private final GalleryModule galleryModule;
    private final SettingsModule settingsModule;

    public Model() {
        this.comparerModule = new ComparerModule();
        this.comparerFileModule = new ComparerFileModule(comparerModule);
        this.galleryModule = new GalleryModule();
        this.settingsModule = new SettingsModule();
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

    public ComparerFileModule getComparerFileModule() {
        return comparerFileModule;
    }
}
