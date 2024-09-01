package pl.magzik;

import pl.magzik.modules.comparer.list.ComparerListModule;
import pl.magzik.modules.comparer.processing.ComparerModule;
import pl.magzik.modules.gallery.management.GalleryManagementModule;
import pl.magzik.modules.settings.SettingsModule;
import pl.magzik.modules.comparer.persistence.ComparerFileModule;
import pl.magzik.modules.gallery.operations.GalleryOperationsModule;
import pl.magzik.modules.gallery.persistence.GalleryFileModule;

public class Model {
    private final ComparerModule comparerModule;
    private final ComparerFileModule comparerFileModule;
    private final ComparerListModule comparerListModule;
    private final GalleryManagementModule galleryManagementModule;
    private final GalleryFileModule galleryFileModule;
    private final GalleryOperationsModule galleryOperationsModule;
    private final SettingsModule settingsModule;

    public Model() {
        this.comparerModule = new ComparerModule();
        this.comparerFileModule = new ComparerFileModule();
        this.comparerListModule = new ComparerListModule();
        this.galleryManagementModule = new GalleryManagementModule();
        this.galleryFileModule = new GalleryFileModule();
        this.galleryOperationsModule = new GalleryOperationsModule();
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

    public GalleryManagementModule getGalleryModule() {
        return galleryManagementModule;
    }

    public GalleryFileModule getGalleryFileModule() {
        return galleryFileModule;
    }

    public GalleryOperationsModule getGalleryOperationsModule() {
        return galleryOperationsModule;
    }

    public SettingsModule getSettingsModule() {
        return settingsModule;
    }
}
