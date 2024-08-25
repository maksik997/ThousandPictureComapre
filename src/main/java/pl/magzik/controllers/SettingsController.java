/*TODO*/

package pl.magzik.controllers;

import pl.magzik.controllers.localization.TranslationInterface;
import pl.magzik.modules.ComparerInterface;
import pl.magzik.modules.ComparerModule;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.SettingsModule;
import pl.magzik.ui.components.settings.ComboBoxSettingsEntry;
import pl.magzik.ui.components.settings.SettingsEntry;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.SettingsView;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SettingsController {
    private final SettingsView sView;
    private final SettingsModule sModule;
    private final GalleryModule gModule;
    private final TranslationInterface ti;
    private final MessageInterface mi;

    private final List<ComparerInterface> cis;

    public SettingsController(SettingsView sView, SettingsModule sModule, GalleryModule gModule, TranslationInterface ti, MessageInterface mi, ComparerInterface... cis) {
        this.sView = sView;
        this.sModule = sModule;
        this.gModule = gModule;
        this.ti = ti;
        this.mi = mi;
        this.cis = Arrays.asList(cis);

        // Save Button
        sView.getSaveButton().addActionListener(_ -> updateSettings());

        initializeSettings();

        // Comparer Module settings init
        for (ComparerInterface c : cis) {
            updateComparerSettings(c);
        }

        // Gallery Module settings init
        updateComparerSettings(gModule);

        gModule.setNameTemplate(
            sModule.getSetting("unify-names-prefix")
        );
        gModule.setLowercaseExtension(
            sModule.getSetting("unify-names-lowercase").equals("yes")
        );

        sView.getSaveButton().setEnabled(false);
    }

    private void updateComparerSettings(ComparerInterface ci) {
        ci.setDestination(new File(sModule.getSetting("destination-for-pc")));
        ci.setMode(
            sModule.getSetting("mode").equals("recursive") ? ComparerModule.Mode.RECURSIVE : ComparerModule.Mode.NON_RECURSIVE
        );
        ci.setPHash(
            sModule.getSetting("phash").equals("yes")
        );
        ci.setPixelByPixel(
            sModule.getSetting("pbp").equals("yes")
        );
    }

    /**
     * Initializes given {@link ComboBoxSettingsEntry} with given setting and sets its value to key.
     * @param setting A {@link String} that represents values possible in combobox.
     * @param entry A {@link ComboBoxSettingsEntry} entry to be initialized.
     * @param key A {@link String} that represents value to be selected in combobox.
     * @throws NullPointerException If any argument is null.
     * */
    private void comboBoxInitialization(String setting, ComboBoxSettingsEntry entry, String key) {
        Objects.requireNonNull(setting);
        Objects.requireNonNull(entry);
        Objects.requireNonNull(key);

        String[] values = sModule.getSetting(setting).split(",");

        entry.initializeComboBox(
            Arrays.stream(values)
                .map(l -> ti.translate(key + "." + l))
                .toArray(String[]::new)
        );

        String value = key + "." + sModule.getSetting(key);
        entry.setValue(
            ti.translate(value)
        );
    }

    /**
     * Initializes settings in view.
     * */
    private void initializeSettings() {
        comboBoxInitialization("languages", sView.getLanguageEntry(), "language");
        comboBoxInitialization("themes", sView.getThemeEntry(), "theme");

        sView.getDestinationEntry().setValue(
            sModule.getSetting("destination-for-pc")
        );

        sView.getRecursiveModeEntry().setValue(
            sModule.getSetting("mode").equals("recursive")
        );

        sView.getPHashModeEntry().setValue(
            sModule.getSetting("phash").equals("yes")
        );

        sView.getPixelByPixelModeEntry().setValue(
            sModule.getSetting("pbp").equals("yes")
        );

        // Gallery's settings init
        sView.getNamesPrefixEntry().setValue(
            sModule.getSetting("unify-names-prefix")
        );

        sView.getNamesLowerCaseEntry().setValue(
            sModule.getSetting("unify-names-lowercase").equals("yes")
        );
    }

    /**
     * Updates settings in model.
     * */
    private void updateSettings() {
        // TODO was there

        boolean messageNeeded = updateIfChanged("language", sView.getLanguageEntry());
        messageNeeded |= updateIfChanged("theme", sView.getThemeEntry());

        if (!sModule.getSetting("destination-for-pc").equals(sView.getDestinationEntry().getValue())) {
            sModule.updateSetting("destination-for-pc", sView.getDestinationEntry().getValue());
        }

        // Update mode
        sModule.updateSetting(
            "mode",
            sView.getRecursiveModeEntry().getValue() ? "recursive" : "not-recursive"
        );

        // Update phash
        sModule.updateSetting(
            "phash",
            sView.getPHashModeEntry().getValue() ? "yes" : "no"
        );

        // Update pbp
        sModule.updateSetting(
            "pbp",
            sView.getPixelByPixelModeEntry().getValue() ? "yes" : "no"
        );

        for (ComparerInterface c : cis) {
            updateComparerSettings(c);
        }

        // Check if the unify names prefix has changed.
        if (!sView.getNamesPrefixEntry().getValue().equals(sModule.getSetting("unify-names-prefix"))) {
            sModule.updateSetting(
                "unify-names-prefix",
                sView.getNamesPrefixEntry().getValue()
            );

            gModule.setNameTemplate(
                sModule.getSetting("unify-names-prefix")
            );
        }

        // Update unify names lowercase.
        sModule.updateSetting(
            "unify-names-lowercase",
            sView.getNamesLowerCaseEntry().getValue() ? "yes" : "no"
        );

        gModule.setLowercaseExtension(
            sModule.getSetting("unify-names-lowercase").equals("yes")
        );

        // Save settings and show the message if needed.
        sModule.saveSettings();
        if (messageNeeded) {
            mi.showInformationMessage(
                ti.translate("message.restart_required.desc"),
                ti.translate("message.restart_required.title")
            );
        }
    }

    /**
     * Updates setting of given key if changed and informs that message is necessary.
     * @param key A {@link String} to be used for searching the setting.
     * @param entry A {@link SettingsEntry} for setting to be found.
     * @return {@code true} if the setting has changed, {@code false} otherwise.
     * */
    private boolean updateIfChanged(String key, SettingsEntry<?,String> entry) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(entry);

        String k = ti.translate(key + "." + sModule.getSetting(key));
        if (!k.equals(entry.getValue())) {
            String[] splitKey = ti.reverseTranslate(
                entry.getValue()
            ).split("\\.");

            sModule.updateSetting(key, splitKey[splitKey.length-1]);
            return true;
        }
        return false;
    }
}
