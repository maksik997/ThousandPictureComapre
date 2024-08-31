package pl.magzik;

import pl.magzik.modules.comparer.list.ComparerListModule;
import pl.magzik.modules.comparer.processing.ComparerModule;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.SettingsModule;
import pl.magzik.modules.comparer.file.ComparerFileModule;
import pl.magzik.modules.gallery.persistence.GalleryFileModule;

public class Model {
    private final ComparerModule comparerModule;
    private final ComparerFileModule comparerFileModule;
    private final ComparerListModule comparerListModule;
    private final GalleryModule galleryModule;
    private final GalleryFileModule galleryFileModule;
    private final SettingsModule settingsModule;

    public Model() {
        this.comparerModule = new ComparerModule();
        this.comparerFileModule = new ComparerFileModule();
        this.comparerListModule = new ComparerListModule();
        this.galleryModule = new GalleryModule();
        this.galleryFileModule = new GalleryFileModule();
        this.settingsModule = new SettingsModule();
    }

    public ComparerModule getComparerModule() {
        return comparerModule;
    }

    public ComparerFileModule getComparerFileModule() {
        return comparerFileModule;
    }

    public ComparerListModule getComparerListModule() {
        return comparerListModule;
    }

    public GalleryModule getGalleryModule() {
        return galleryModule;
    }

    public GalleryFileModule getGalleryFileModule() {
        return galleryFileModule;
    }

    public SettingsModule getSettingsModule() {
        return settingsModule;
    }
}
