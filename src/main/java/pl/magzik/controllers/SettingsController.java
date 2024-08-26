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

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The {@code SettingsController} class manages the interaction between the settings user interface and the underlying settings module.
 * It is responsible for initializing the settings view, handling user input, and updating settings both in the internal settings module
 * and external components such as {@link ComparerInterface} instances and the {@link GalleryModule}.
 * <p>
 * This controller listens for changes in settings and enables or disables the save button accordingly. When the user clicks the save button,
 * it updates the settings in the settings module and other related modules if necessary. If any settings have changed and require a restart,
 * it notifies the user through an information message.
 * </p>
 * <p>
 * The main responsibilities of this controller include:
 * <ul>
 *   <li>Initializing settings in the view based on the current settings module values.</li>
 *   <li>Setting up listeners to detect changes in settings and enable the save button when needed.</li>
 *   <li>Updating settings in the settings module and external components when changes are saved.</li>
 *   <li>Handling errors that may occur during the settings save process.</li>
 *   <li>Notifying the user when a restart is required due to changes in settings.</li>
 * </ul>
 * </p>
 * <p>
 * The controller depends on several interfaces and modules:
 * <ul>
 *   <li>{@link SettingsView} - The view component that provides the user interface for settings.</li>
 *   <li>{@link SettingsModule} - The module responsible for storing and managing the settings data.</li>
 *   <li>{@link GalleryModule} - A module that also implements {@link ComparerInterface} and is affected by certain settings.</li>
 *   <li>{@link TranslationInterface} - For translating strings used in the settings view.</li>
 *   <li>{@link MessageInterface} - For displaying messages to the user.</li>
 *   <li>{@link ComparerInterface} - An interface representing modules that require updates based on settings.</li>
 * </ul>
 * </p>
 * <p>
 * This class ensures that the settings are synchronized between the user interface, internal settings module, and any external components
 * that rely on these settings. It handles user actions and updates the system state as required.
 * </p>
 */
public class SettingsController {
    private final SettingsView sView;
    private final SettingsModule sModule;
    private final GalleryModule gModule;
    private final TranslationInterface ti;
    private final MessageInterface mi;

    private final List<ComparerInterface> cis;

    /**
     * Constructs a new SettingsController with the given dependencies and initializes the settings view.
     *
     * @param sView The {@link SettingsView} instance to interact with the user interface.
     * @param sModule The {@link SettingsModule} instance to handle the settings data.
     * @param gModule The {@link GalleryModule} instance, which also implements {@link ComparerInterface}.
     * @param ti The {@link TranslationInterface} instance for translating strings.
     * @param mi The {@link MessageInterface} instance for showing messages.
     * @param cis An array of {@link ComparerInterface} implementations, which may include {@link GalleryModule}.
     */
    public SettingsController(SettingsView sView, SettingsModule sModule, GalleryModule gModule, TranslationInterface ti, MessageInterface mi, ComparerInterface... cis) {
        this.sView = sView;
        this.sModule = sModule;
        this.gModule = gModule;
        this.ti = ti;
        this.mi = mi;
        this.cis = Arrays.asList(cis);

        // Initialize settings and update external settings.
        initializeSettings();
        updateExternalSettings();

        // Set up the save button: attach an action listener and disable it initially.
        sView.getSaveButton().addActionListener(_ -> updateSettings());
        sView.getSaveButton().setEnabled(false);

        // Add property change listeners to update the save button state.
        addPropertyChangeListeners();
    }

    /**
     * Initializes the settings view with the current values from the settings module.
     * Sets up combo boxes and other settings controls based on stored settings.
     */
    private void initializeSettings() {
        initializeComboboxSetting("language", "languages", sView.getLanguageEntry());
        initializeComboboxSetting("theme", "themes", sView.getThemeEntry());
        initializeStringSetting("destination-for-pc", sView.getDestinationEntry());
        initializeStringSetting("unify-names-prefix", sView.getNamesPrefixEntry());
        initializeBooleanSetting("recursive-mode", sView.getRecursiveModeEntry());
        initializeBooleanSetting("phash", sView.getPHashModeEntry());
        initializeBooleanSetting("pbp", sView.getPixelByPixelModeEntry());
        initializeBooleanSetting("unify-names-lowercase", sView.getNamesLowerCaseEntry());
    }

    /**
     * Initializes given {@link ComboBoxSettingsEntry} with given setting and sets its value to key.
     * @param key A {@link String} that represents value to be selected in combobox.
     * @param setting A {@link String} that represents values possible in combobox.
     * @param entry A {@link ComboBoxSettingsEntry} entry to be initialized.
     * @throws NullPointerException If any argument is null.
     * */
    private void initializeComboboxSetting(String key, String setting, ComboBoxSettingsEntry entry) {
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
     * Initializes a string setting in the specified settings entry.
     * @param key A {@link String} used for retrieving setting.
     * @param entry A {@link SettingsEntry} to be updated.
     * @throws NullPointerException If any argument is null.
     * */
    private void initializeStringSetting(String key, SettingsEntry<?,String> entry) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(entry);

        String value = sModule.getSetting(key);
        entry.setValue(value != null ? value : "");
    }

    /**
     * Initializes a boolean setting in the specified settings entry.
     * @param key A {@link String} used for retrieving setting.
     * @param entry A {@link SettingsEntry} to be updated.
     * @throws NullPointerException If any argument is null.
     * */
    private void initializeBooleanSetting(String key, SettingsEntry<?,Boolean> entry) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(entry);

        String value = sModule.getSetting(key);
        entry.setValue(value.equals("yes"));
    }


    /**
     * Adds property change listeners to various settings entries in the view.
     * Each listener monitors changes to the "value" property and updates the enabled state
     * of the save button based on whether any of the settings have been changed.
     */
    private void addPropertyChangeListeners() {
        // Create a PropertyChangeListener that enables or disables the save button based on setting changes.
        PropertyChangeListener pcl = e -> {
            if (e.getPropertyName().equals("value")) {
                sView.getSaveButton().setEnabled(hasAnySettingChanged());
            }
        };

        // Register the same PropertyChangeListener for all relevant settings entries.
        sView.getLanguageEntry().addPropertyChangeListener(pcl);
        sView.getThemeEntry().addPropertyChangeListener(pcl);
        sView.getDestinationEntry().addPropertyChangeListener(pcl);
        sView.getRecursiveModeEntry().addPropertyChangeListener(pcl);
        sView.getPHashModeEntry().addPropertyChangeListener(pcl);
        sView.getPixelByPixelModeEntry().addPropertyChangeListener(pcl);
        sView.getNamesLowerCaseEntry().addPropertyChangeListener(pcl);
        sView.getNamesPrefixEntry().addPropertyChangeListener(pcl);
    }

    /**
     * Updates all settings based on the current values in the view and saves the changes.
     * Displays a message to the user if any settings have changed and a restart is required.
     */
    private void updateSettings() {
        BiConsumer<String, String> comboboxBiConsumer = (k, v) -> {
            String[] split = ti.reverseTranslate(v).split("\\.");
            sModule.updateSetting(k, split[split.length - 1]);
        };

        // Updates settings in Settings Module

        boolean messageNeeded = updateSettingIfChanged("language", sView.getLanguageEntry().getValue(), comboboxBiConsumer, v -> ti.translate("language." + v));
        messageNeeded |= updateSettingIfChanged("theme", sView.getThemeEntry().getValue(), comboboxBiConsumer, v -> ti.translate("theme." + v));

        updateStringSettingIfChanged("destination-for-pc", sView.getDestinationEntry().getValue());
        updateBooleanSettingIfChanged("recursive-mode", sView.getRecursiveModeEntry().getValue());
        updateBooleanSettingIfChanged("phash", sView.getPHashModeEntry().getValue());
        updateBooleanSettingIfChanged("pbp", sView.getPixelByPixelModeEntry().getValue());
        updateStringSettingIfChanged("unify-names-prefix", sView.getNamesPrefixEntry().getValue());
        updateBooleanSettingIfChanged("unify-names-lowercase", sView.getNamesLowerCaseEntry().getValue());

        // Updates settings in other modules.

        updateExternalSettings();

        // Saves settings and shows the message if needed.

        try {
            sModule.saveSettings();
        } catch (IOException e) {
            mi.showErrorMessage(
                ti.translate("error.save.ioexception.desc"),
                ti.translate("error.general.title")
            );
            return;
        }

        if (messageNeeded) {
            mi.showInformationMessage(
                ti.translate("message.restart_required.desc"),
                ti.translate("message.restart_required.title")
            );
        }

        sView.getSaveButton().setEnabled(false);
    }

    /**
     * Updates a boolean setting if the provided new value differs from the current one.
     *
     * @param key      A {@link String} used to retrieve and update the setting.
     * @param newValue The new {@link Boolean} value to set if it has changed.
     * @throws NullPointerException If the key or the retrieved current value is null.
     */
    private void updateBooleanSettingIfChanged(String key, boolean newValue) {
        updateSettingIfChanged(
            key,
            newValue,
            (k, v) -> sModule.updateSetting(k, v ? "yes" : "no"),
            v -> v.equals("yes")
        );
    }

    /**
     * Updates a string setting if the provided new value differs from the current one.
     *
     * @param key            A {@link String} used to retrieve and update the setting.
     * @param newValue       The new {@link String} value to set if setting has changed.
     * @throws NullPointerException If the key, newValue or the retrieved current value is {@code null}.
     */
    private void updateStringSettingIfChanged(String key, String newValue) {
        updateSettingIfChanged(key, newValue, sModule::updateSetting, s -> s);
    }

    /**
     * Updates a setting if the new value differs from the current one.
     * This method checks if the new value is different from the current setting value.
     * If so, it updates the setting using the provided updater.
     *
     * @param key A {@link String} used to retrieve the current setting value from the settings module.
     * @param newValue The new value to update the setting with if it has changed. Must not be {@code null}.
     * @param settingUpdater A {@link BiConsumer} that accepts the key and the new value to perform the update.
     * @param valueConverter A {@link Function} that converts the current setting value (retrieved as a {@link String}) to the required type {@code T}.
     * @param <T> The type of the setting value.
     * @return {@code true} if the setting was updated, {@code false} otherwise.
     * @throws NullPointerException If any argument (key, newValue, settingUpdater, valueConverter) or the current value is {@code null}.
     */
    private <T> boolean updateSettingIfChanged(String key, T newValue, BiConsumer<String, T> settingUpdater, Function<String, T> valueConverter) {
        Objects.requireNonNull(valueConverter);

        if (hasSettingChanged(key, newValue, valueConverter)) {
            settingUpdater.accept(key, newValue);
            return true;
        }
        return false;
    }

    /**
     * Checks if any of the settings have changed compared to their current values.
     * This method compares the values of various settings in the view with their corresponding current values in the settings module.
     * If any of the settings differ from their current values, the method returns {@code true}.
     * Otherwise, it returns {@code false}.
     *
     * @return {@code true} if at least one setting has changed, {@code false} otherwise.
     */
    private boolean hasAnySettingChanged() {
        Function<String, Boolean> booleanFunction = v -> v.equals("yes");

        boolean hasSettingChanged = hasSettingChanged("language", sView.getLanguageEntry().getValue(), v -> ti.translate("language." + v));
        hasSettingChanged |= hasSettingChanged("theme", sView.getThemeEntry().getValue(), v -> ti.translate("theme." + v));
        hasSettingChanged |= hasSettingChanged("destination-for-pc", sView.getDestinationEntry().getValue());
        hasSettingChanged |= hasSettingChanged("recursive-mode", sView.getRecursiveModeEntry().getValue(), booleanFunction);
        hasSettingChanged |= hasSettingChanged("phash", sView.getPHashModeEntry().getValue(), booleanFunction);
        hasSettingChanged |= hasSettingChanged("pbp", sView.getPixelByPixelModeEntry().getValue(), booleanFunction);
        hasSettingChanged |= hasSettingChanged("unify-names-prefix", sView.getNamesPrefixEntry().getValue());
        hasSettingChanged |= hasSettingChanged("unify-names-lowercase", sView.getNamesLowerCaseEntry().getValue(), booleanFunction);
        return hasSettingChanged;
    }

    /**
     * Checks if the current setting value differs from the new value.
     * This method retrieves the current setting value,
     * converts it to the required type, and compares it with the new value.
     *
     * @param key A {@link String} used to retrieve the current setting value from the settings module.
     * @param newValue The new value to compare with the current setting value. Must not be {@code null}.
     * @return {@code true} if the current setting value is different from the new value, {@code false} otherwise.
     * @throws NullPointerException If any argument (key, newValue, valueConverter) or the current value is {@code null}.
     */
    private boolean hasSettingChanged(String key, String newValue) {
        return hasSettingChanged(key, newValue, s -> s);
    }

    /**
     * Checks if the current setting value differs from the new value.
     * This method retrieves the current setting value,
     * converts it to the required type, and compares it with the new value.
     *
     * @param key A {@link String} used to retrieve the current setting value from the settings module.
     * @param newValue The new value to compare with the current setting value. Must not be {@code null}.
     * @param valueConverter A {@link Function} that converts the current setting value
     *                       (retrieved as a {@link String}) to the required type {@code T}.
     * @param <T> The type of the setting value.
     * @return {@code true} if the current setting value is different from the new value, {@code false} otherwise.
     * @throws NullPointerException If any argument (key, newValue, valueConverter) or the current value is {@code null}.
     */
    private <T> boolean hasSettingChanged(String key, T newValue, Function<String, T> valueConverter) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(newValue);
        Objects.requireNonNull(valueConverter);

        T currentValue = valueConverter.apply(sModule.getSetting(key));
        Objects.requireNonNull(currentValue);

        return !currentValue.equals(newValue);
    }

    /**
     * Updates external settings and configurations based on the current values in the settings module.
     * This method performs the following actions:
     * <ul>
     *   <li>Sets the name template for the {@link GalleryModule} using the value of the "unify-names-prefix" setting.</li>
     *   <li>Configures whether the extensions should be lowercase based on the "unify-names-lowercase" setting.</li>
     *   <li>Iterates over a collection of {@link ComparerInterface} instances and applies settings updates using the {@link #updateComparerSettings(ComparerInterface)} method.</li>
     * </ul>
     *
     * The method ensures that the external components and modules are updated to reflect the current settings.
     */
    private void updateExternalSettings() {
        gModule.setNameTemplate(sModule.getSetting("unify-names-prefix"));
        gModule.setLowercaseExtension(sModule.getSetting("unify-names-lowercase").equals("yes"));

        for (ComparerInterface c : cis) {
            updateComparerSettings(c);
        }
    }

    /**
     * Updates the settings of the given {@link ComparerInterface} instance based on the current values from the settings module.
     * This method performs the following updates on the provided comparer interface:
     * <ul>
     *   <li>Sets the destination directory using the "destination-for-pc" setting value.</li>
     *   <li>Configures the mode of the comparer based on the "recursive-mode" setting. The mode is set to {@link ComparerModule.Mode#RECURSIVE} if the setting value is "yes", otherwise it is set to {@link ComparerModule.Mode#NON_RECURSIVE}.</li>
     *   <li>Sets the perceptual hash comparison flag based on the "phash" setting. The flag is set to {@code true} if the setting value is "yes".</li>
     *   <li>Configures pixel-by-pixel comparison based on the "pbp" setting. The flag is set to {@code true} if the setting value is "yes".</li>
     * </ul>
     *
     * @param ci A {@link ComparerInterface} instance to be updated. Must not be {@code null}.
     * @throws NullPointerException If the {@code ci} parameter is {@code null}.
     */
    private void updateComparerSettings(ComparerInterface ci) {
        Objects.requireNonNull(ci);

        // Retrieve settings
        String destinationPath = sModule.getSetting("destination-for-pc"),
        recursiveMode = sModule.getSetting("recursive-mode"),
        pHash = sModule.getSetting("phash"),
        pbp = sModule.getSetting("pbp");

        // Set settings.
        ci.setDestination(new File(destinationPath));
        ci.setMode(
            recursiveMode.equals("yes") ? ComparerModule.Mode.RECURSIVE : ComparerModule.Mode.NON_RECURSIVE
        );
        ci.setPHash(pHash.equals("yes"));
        ci.setPixelByPixel(pbp.equals("yes"));
    }
}
