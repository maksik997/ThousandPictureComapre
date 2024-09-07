package pl.magzik;

import pl.magzik.modules.comparer.ComparerCoordinator;
import pl.magzik.modules.gallery.GalleryCoordinator;
import pl.magzik.modules.settings.SettingsModule;

public class Model {
    private final GalleryCoordinator gc;
    private final ComparerCoordinator cc;
    private final SettingsModule settingsModule;

    public Model() {
        this.settingsModule = new SettingsModule();

        this.gc = new GalleryCoordinator();
        this.cc = new ComparerCoordinator();
    }

    public SettingsModule getSettingsModule() {
        return settingsModule;
    }

    public GalleryCoordinator getGc() {
        return gc;
    }

    public ComparerCoordinator getCc() {
        return cc;
    }
}
