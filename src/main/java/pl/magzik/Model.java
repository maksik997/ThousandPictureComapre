package pl.magzik;

import pl.magzik.modules.comparer.list.ComparerListModule;
import pl.magzik.modules.comparer.processing.ComparerModule;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.SettingsModule;
import pl.magzik.modules.comparer.file.ComparerFileModule;

public class Model {
    private final ComparerModule comparerModule;
    private final ComparerFileModule comparerFileModule;
    private final GalleryModule galleryModule;
    private final SettingsModule settingsModule;
    private final ComparerListModule comparerListModule;

    public Model() {
        this.comparerModule = new ComparerModule();
        this.comparerFileModule = new ComparerFileModule();
        this.comparerListModule = new ComparerListModule();
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

    public ComparerListModule getComparerListModule() {
        return comparerListModule;
    }
}
