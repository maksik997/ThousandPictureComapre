/*TODO*/

package pl.magzik.controllers;

import pl.magzik.controllers.localization.TranslationInterface;
import pl.magzik.modules.ComparerInterface;
import pl.magzik.modules.ComparerModule;
import pl.magzik.modules.GalleryModule;
import pl.magzik.modules.SettingsModule;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.SettingsView;

import java.io.File;
import java.util.Arrays;

public class SettingsController {
    private final SettingsView sView;
    private final SettingsModule sModule;
    private final ComparerModule cModule;
    private final GalleryModule gModule;
    private final TranslationInterface ti;
    private final MessageInterface mi;

    public SettingsController(SettingsView sView, SettingsModule sModule, ComparerModule cModule, GalleryModule gModule, TranslationInterface ti, MessageInterface mi) {
        this.sView = sView;
        this.sModule = sModule;
        this.cModule = cModule;
        this.gModule = gModule;
        this.ti = ti;
        this.mi = mi;

        // Save Button
        sView.getSaveButton().addActionListener(_ -> {
            boolean messageNeeded = false;

            // Check if the language has changed.
            String lang = ti.translate("lang." + sModule.getSetting("language"));
            if (!lang.equals(sView.getLanguageEntry().getValue())) {
                messageNeeded = true;

                String[] splitKey = ti.reverseTranslate(
                        (String) sView.getLanguageEntry().getValue()
                ).split("\\.");
                sModule.updateSetting("language", splitKey[splitKey.length-1]);
            }

            // Check if the theme has changed.
            String theme = ti.translate("theme." + sModule.getSetting("theme"));
            if (!theme.equals(sView.getThemeEntry().getValue())) {
                messageNeeded = true;

                String[] splitKey = ti.reverseTranslate(
                        (String) sView.getThemeEntry().getValue()
                ).split("\\.");
                sModule.updateSetting("theme", splitKey[splitKey.length-1]);
            }

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

            updateComparerSettings(cModule);
            updateComparerSettings(gModule);


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
        });

        // Language setting initialization
        String[] languages = sModule.getSetting("languages").split(",");

        sView.getLanguageEntry().initializeComboBox(
                Arrays.stream(languages)
                        .map(l -> ti.translate("lang." + l))
                        .toArray(String[]::new)
        );

        String language = "lang." + sModule.getSetting("language");

        sView.getLanguageEntry().setValue(ti.translate(language));

        // Theme setting initialization
        String[] themes = sModule.getSetting("themes").split(",");

        sView.getThemeEntry().initializeComboBox(
                Arrays.stream(themes)
                        .map(l -> ti.translate("theme." + l))
                        .toArray(String[]::new)
        );

        String theme = "theme." + sModule.getSetting("theme");
        sView.getThemeEntry().setValue(
                ti.translate(theme)
        );


        // Comparer's settings init
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

        // Comparer Module settings init
        updateComparerSettings(cModule);

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
}
