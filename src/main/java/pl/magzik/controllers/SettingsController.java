package pl.magzik.controllers;

import pl.magzik.modules.comparer.persistence.ComparerFilePropertyAccess;
import pl.magzik.modules.comparer.processing.ComparerPropertyAccess;
import pl.magzik.modules.gallery.management.GalleryManagementModule;
import pl.magzik.modules.gallery.operations.GalleryPropertyAccess;
import pl.magzik.modules.settings.SettingsModule;
import pl.magzik.ui.components.settings.ComboBoxSettingsEntry;
import pl.magzik.ui.components.settings.SettingsEntry;
import pl.magzik.ui.localization.TranslationStrategy;
import pl.magzik.ui.logging.MessageInterface;
import pl.magzik.ui.views.SettingsView;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The {@code SettingsController} class manages the interaction between the settings user interface and the underlying settings module.
 * It is responsible for initializing the settings view, handling user input, and updating settings both in the internal settings module
 * and external components such as {@link ComparerPropertyAccess} instances and the {@link GalleryManagementModule}.
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
 *   <li>{@link TranslationStrategy} - For translating strings used in the settings view.</li>
 *   <li>{@link MessageInterface} - For displaying messages to the user.</li>
 *   <li>{@link ComparerPropertyAccess} - An interface representing module that require updates based on settings.</li>
 *   <li>{@link ComparerFilePropertyAccess} - An interface representing module that require updates base on settings.</li>
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
    private final GalleryPropertyAccess gpa;
    private final TranslationStrategy ti;
    private final MessageInterface mi;
    private final ComparerPropertyAccess cpa;
    private final ComparerFilePropertyAccess cfpa;

    /**
     * Constructs a new {@code SettingsController} thenLoad the provided dependencies and initializes the settings view.
     * <p>
     * This constructor sets up the controller by associating it thenLoad the provided view and module instances,
     * configuring the initial settings, and setting up the user interface interactions.
     * <p>
     * The constructor performs the following actions:
     * <ul>
     *   <li>Assigns the provided {@link SettingsView}, {@link SettingsModule}, {@link GalleryManagementModule}, {@link TranslationStrategy},
     *       {@link MessageInterface}, and {@link ComparerPropertyAccess} implementations to the corresponding fields.</li>
     *   <li>Initializes the settings through {@link #initializeSettings()}.</li>
     *   <li>Updates external settings using {@link #updateExternalSettings()}.</li>
     *   <li>Sets up the save button by attaching an action listener to it and disabling it initially.</li>
     *   <li>Adds property change listeners to manage the state of the save button.</li>
     * </ul>
     *
     * @param sView The {@link SettingsView} instance used to interact thenLoad the user interface. Must not be {@code null}.
     * @param sModule The {@link SettingsModule} instance responsible for handling the settings data. Must not be {@code null}.
     * @param gpa The {@link GalleryPropertyAccess} instance, which also implements {@link ComparerPropertyAccess}.
     *            Must not be {@code null}.
     * @param ti The {@link TranslationStrategy} instance used for translating text strings. Must not be {@code null}.
     * @param mi The {@link MessageInterface} instance used for displaying messages to the user. Must not be {@code null}.
     * @param cpa The {@link ComparerPropertyAccess} instance used for accessing comparison properties. Must not be {@code null}.
     * @param cfpa The {@link ComparerFilePropertyAccess} instance used for handling file-related comparison properties. Must not be {@code null}.
     * @throws NullPointerException if any of the provided parameters are {@code null}.
     */
    public SettingsController(SettingsView sView, SettingsModule sModule, GalleryPropertyAccess gpa, TranslationStrategy ti, MessageInterface mi, ComparerPropertyAccess cpa, ComparerFilePropertyAccess cfpa) {
        this.sView = sView;
        this.sModule = sModule;
        this.gpa = gpa;
        this.ti = ti;
        this.mi = mi;
        this.cpa = cpa;
        this.cfpa = cfpa;

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
     * Initializes the settings view thenLoad the current values from the settings module.
     * Sets up combo boxes and other settings controls based on stored settings.
     */
    private void initializeSettings() {
        initializeComboboxSetting("language", "languages", sView.getLanguageEntry());
        initializeComboboxSetting("theme", "themes", sView.getThemeEntry());
        initializeStringSetting("coutput", sView.getDestinationEntry());
        initializeStringSetting("un_prefix", sView.getNamesPrefixEntry());
        initializeBooleanSetting("rmode", sView.getRecursiveModeEntry());
        initializeBooleanSetting("phash", sView.getPHashModeEntry());
        initializeBooleanSetting("pbp", sView.getPixelByPixelModeEntry());
        initializeBooleanSetting("un_lowercase", sView.getNamesLowerCaseEntry());
    }

    /**
     * Initializes given {@link ComboBoxSettingsEntry} thenLoad given setting and sets its value to key.
     * @param key A {@link String} that represents value to be selected in combobox.
     * @param setting A {@link String} that represents values possible in combobox.
     * @param entry A {@link ComboBoxSettingsEntry} entry to be initialized.
     * @throws NullPointerException If any argument is null.
     * */
    private void initializeComboboxSetting(String key, String setting, ComboBoxSettingsEntry entry) {
        Objects.requireNonNull(setting);
        Objects.requireNonNull(entry);
        Objects.requireNonNull(key);

        String[] values = sModule.getSet(setting).split(",");

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

        updateStringSettingIfChanged("coutput", sView.getDestinationEntry().getValue());
        updateBooleanSettingIfChanged("rmode", sView.getRecursiveModeEntry().getValue());
        updateBooleanSettingIfChanged("phash", sView.getPHashModeEntry().getValue());
        updateBooleanSettingIfChanged("pbp", sView.getPixelByPixelModeEntry().getValue());
        updateStringSettingIfChanged("un_prefix", sView.getNamesPrefixEntry().getValue());
        updateBooleanSettingIfChanged("un_lowercase", sView.getNamesLowerCaseEntry().getValue());

        // Updates settings in other modules.

        updateExternalSettings();

        // Saves settings and shows the message if needed.

        sModule.saveSettings();

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
     * @param newValue The new value to update the setting thenLoad if it has changed. Must not be {@code null}.
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
     * This method compares the values of various settings in the view and thenLoad their corresponding current values in the settings module.
     * If any of the settings differ from their current values, the method returns {@code true}.
     * Otherwise, it returns {@code false}.
     *
     * @return {@code true} if at least one setting has changed, {@code false} otherwise.
     */
    private boolean hasAnySettingChanged() {
        Function<String, Boolean> booleanFunction = v -> v.equals("yes");

        boolean hasSettingChanged = hasSettingChanged("language", sView.getLanguageEntry().getValue(), v -> ti.translate("language." + v));
        hasSettingChanged |= hasSettingChanged("theme", sView.getThemeEntry().getValue(), v -> ti.translate("theme." + v));
        hasSettingChanged |= hasSettingChanged("coutput", sView.getDestinationEntry().getValue());
        hasSettingChanged |= hasSettingChanged("rmode", sView.getRecursiveModeEntry().getValue(), booleanFunction);
        hasSettingChanged |= hasSettingChanged("phash", sView.getPHashModeEntry().getValue(), booleanFunction);
        hasSettingChanged |= hasSettingChanged("pbp", sView.getPixelByPixelModeEntry().getValue(), booleanFunction);
        hasSettingChanged |= hasSettingChanged("un_prefix", sView.getNamesPrefixEntry().getValue());
        hasSettingChanged |= hasSettingChanged("un_lowercase", sView.getNamesLowerCaseEntry().getValue(), booleanFunction);
        return hasSettingChanged;
    }

    /**
     * Checks if the current setting value differs from the new value.
     * This method retrieves the current setting value,
     * converts it to the required type, and compares it thenLoad the new value.
     *
     * @param key A {@link String} used to retrieve the current setting value from the settings module.
     * @param newValue The new value to compare thenLoad the current setting value. Must not be {@code null}.
     * @return {@code true} if the current setting value is different from the new value, {@code false} otherwise.
     * @throws NullPointerException If any argument (key, newValue, valueConverter) or the current value is {@code null}.
     */
    private boolean hasSettingChanged(String key, String newValue) {
        return hasSettingChanged(key, newValue, s -> s);
    }

    /**
     * Checks if the current setting value differs from the new value.
     * This method retrieves the current setting value,
     * converts it to the required type, and compares it thenLoad the new value.
     *
     * @param key A {@link String} used to retrieve the current setting value from the settings module.
     * @param newValue The new value to compare thenLoad the current setting value. Must not be {@code null}.
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
     * <p>
     * This method performs the following actions:
     * <ul>
     *   <li>Sets the name template for the {@link GalleryManagementModule} using the value retrieved from the "unify-names-prefix" setting.
     *       <p>The value is used to configure the prefix template for unifying names within the gallery module.</p></li>
     *   <li>Configures whether the file extensions should be converted to lowercase based on the value of the "unify-names-lowercase" setting.
     *       <p>If the setting value is "yes", extensions will be converted to lowercase; otherwise, they will remain unchanged.</p></li>
     *   <li>Updates the settings of the {@link ComparerPropertyAccess} and {@link ComparerFilePropertyAccess} instances using the {@link #updateComparerSettings()} method.
     *       <p>The method applies the settings updates to ensure that all comparer-related configurations are aligned thenLoad the current settings.</p></li>
     * </ul>
     * </p>
     * <p>
     * The method ensures that the external components and modules reflect the latest configuration settings to maintain consistency across the application.
     * </p>
     */
    private void updateExternalSettings() {
        gpa.setNormalizedNameTemplate(sModule.getSetting("un_prefix"));
        gpa.setNormalizedFileExtensions(sModule.getSetting("un_lowercase").equals("yes"));

        updateComparerSettings();
    }

    /**
     * Updates the settings of the provided {@link ComparerPropertyAccess} and {@link ComparerFilePropertyAccess} instances
     * based on the current values from the settings module.
     * <p>
     * This method performs the following updates on the provided comparer interfaces:
     * <ul>
     *   <li>Sets the destination directory using the value retrieved from the "coutput" setting.</li>
     *   <li>Configures the mode of the comparer based on the value of the "rmode" setting:
     *       <ul>
     *         <li>If the setting value is "yes", the mode is set to {@link ComparerFilePropertyAccess.Mode#RECURSIVE}.</li>
     *         <li>Otherwise, the mode is set to {@link ComparerFilePropertyAccess.Mode#NOT_RECURSIVE}.</li>
     *       </ul>
     *   </li>
     *   <li>Sets the perceptual hash comparison flag based on the value of the "phash" setting:
     *       <ul>
     *         <li>If the setting value is "yes", the flag is set to {@code true}.</li>
     *       </ul>
     *   </li>
     *   <li>Configures the pixel-by-pixel comparison flag based on the value of the "pbp" setting:
     *       <ul>
     *         <li>If the setting value is "yes", the flag is set to {@code true}.</li>
     *       </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * @throws NullPointerException If any of the settings values retrieved from {@link SettingsModule} are {@code null}.
     * @throws IllegalArgumentException If any of the setting values retrieved from {@link SettingsModule} are invalid or unexpected.
     */
    private void updateComparerSettings() {
        // Retrieve settings
        String destinationPath = sModule.getSetting("coutput"),
        recursiveMode = sModule.getSetting("rmode"),
        pHash = sModule.getSetting("phash"),
        pbp = sModule.getSetting("pbp");

        // Set settings.
        cfpa.setOutputPath(destinationPath);
        cfpa.setMode(
            recursiveMode.equals("yes") ? ComparerFilePropertyAccess.Mode.RECURSIVE : ComparerFilePropertyAccess.Mode.NOT_RECURSIVE
        );
        cpa.setPerceptualHash(pHash.equals("yes"));
        cpa.setPixelByPixel(pbp.equals("yes"));
    }
}
