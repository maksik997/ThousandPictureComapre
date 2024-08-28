package pl.magzik;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import pl.magzik.modules.loader.ModuleLoader;
import pl.magzik.modules.theme.ThemeDetector;
import pl.magzik.ui.views.LoadingFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Entry point of the application that initializes and starts the application.
 * <p>
 * This class sets up the application environment, including loading configuration settings,
 * initializing modules, setting up UI properties, and launching the main application view.
 * </p>
 */
public class Main {

    /**
     * The main method that serves as the entry point for the application.
     * <p>
     * It initializes the model, sets up the module loader, loads initial modules,
     * configures the UI, and launches the application view. If an error occurs during
     * initialization or loading, it is handled by displaying an error message and exiting the application.
     * </p>
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            Model model = initializeModel();
            ModuleLoader moduleLoader = initializeModuleLoader(model);
            loadInitialModule(moduleLoader, model);

            setupUIManagerProperties();
            setupLookAndFeel(model.getSettingsModule().getSetting("theme"));

            Locale locale = getLocale(model);
            View view = initializeView(locale);
            configureView(view);

            LoadingFrame loadingFrame = createLoadingFrame(view, moduleLoader);

            loadModules(view, moduleLoader);
            Controller controller = initializeController(view, model, locale);

            launchApplication(view, loadingFrame);
        } catch (IOException e) {
            handleError(e);
        }
    }

    /**
     * Initializes the {@code Model} instance.
     * @return a new instance of {@code Model}
     * @throws IOException if an error occurs while initializing the model
     */
    private static Model initializeModel() throws IOException {
        return new Model();
    }

    /**
     * Initializes the {@code ModuleLoader} with modules from the provided {@code Model}.
     * @param model the {@code Model} instance from which modules are retrieved
     * @return a configured {@code ModuleLoader}
     * @throws NullPointerException if {@code model} is {@code null}
     */
    private static ModuleLoader initializeModuleLoader(Model model) {
        Objects.requireNonNull(model);

        return ModuleLoader.create(model.getSettingsModule())
                    .with(model.getComparerModule())
                    .with(model.getGalleryModule())
                    .ready();
    }

    /**
     * Loads the initial module using the provided {@code ModuleLoader} and {@code Model}.
     * @param moduleLoader the {@code ModuleLoader} used to load the initial module
     * @param model the {@code Model} instance used to load settings
     * @throws IOException if an error occurs while loading the module or settings
     * @throws NullPointerException if {@code moduleLoader} or {@code model} is {@code null}
     */
    private static void loadInitialModule(ModuleLoader moduleLoader, Model model) throws IOException {
        Objects.requireNonNull(moduleLoader);
        Objects.requireNonNull(model);

        moduleLoader.loadNext(); // Settings Module
        model.getSettingsModule().loadSettings();
    }

    /**
     * Configures UIManager properties for custom look and feel.
     */
    private static void setupUIManagerProperties(){
        UIManager.put("Button.arc", 999);
        UIManager.put("Component.arc", 999);
        UIManager.put("TextComponent.arc", 999);
        UIManager.put("Component.arrowType", "chevron");
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Button.innerFocusWidth", 0);
    }

    /**
     * Configures the application's look and feel based on the specified theme.
     * <p>
     * This method sets the look and feel of the application to either a dark or light theme
     * based on the value of the {@code theme} parameter. If the specified theme is "system",
     * it will determine the appropriate theme based on the current system settings.
     * On non-Windows platforms, using the "system" theme will throw an exception.
     * </p>
     *
     * @param theme the theme to apply. Can be "dark", "light", or "system".
     *              - "dark" for dark theme,
     *              - "light" for light theme,
     *              - "system" for the theme based on the current system settings (Windows only).
     * @throws NullPointerException if {@code theme} is {@code null}.
     * @throws UnsupportedOperationException if {@code theme} is "system" and the platform is not Windows.
     */
    private static void setupLookAndFeel(String theme) {
        Objects.requireNonNull(theme, "Settings module isn't loaded.");

        if (theme.equalsIgnoreCase("system")) {
            if (!SystemInfo.isWindows) handleError(new UnsupportedOperationException("Unsupported system theme setting."));
            theme = ThemeDetector.isDarkTheme() ? "dark" : "light";
        }

        if (theme.equalsIgnoreCase("dark")) FlatDarculaLaf.setup();
        else FlatLightLaf.setup();
    }

    /**
     * Retrieves the locale for the application based on the model settings.
     * @param model the {@code Model} instance used to get the language setting
     * @return the {@code Locale} instance for the application
     * @throws NullPointerException if {@code model} is {@code null}
     */
    private static Locale getLocale(Model model) {
        Objects.requireNonNull(model);

        return Locale.forLanguageTag(model.getSettingsModule().getSetting("language"));
    }

    /**
     * Initializes the {@code View} instance with the specified locale.
     * @param locale the {@code Locale} to use it for resource bundles
     * @return a new {@code View} instance
     * @throws NullPointerException if {@code locale} is {@code null}
     */
    private static View initializeView(Locale locale) {
        Objects.requireNonNull(locale);

        ResourceBundle resources = ResourceBundle.getBundle("localization", locale);

        return new View(resources);
    }

    /**
     * Configures the {@link View} for macOS-specific properties.
     * <p>
     * This method checks if the application is running on macOS and, if so, sets several macOS-specific
     * system properties to customize the appearance and behavior of the application window. These properties include:
     * </p>
     * <ul>
     *     <li>{@code apple.awt.application.name} - Sets the name of the application as it appears in the macOS menu bar.</li>
     *     <li>{@code apple.awt.application.appearance} - Configures the appearance of the application (e.g., light or dark mode).</li>
     *     <li>{@code apple.awt.windowTitleVisible} - Controls whether the window title is visible.</li>
     * </ul>
     * <p>
     * If the application is not running on macOS, this method does nothing.
     * </p>
     *
     * @param view the {@link View} instance to be configured
     */
    private static void configureView(View view) {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.awt.application.name", "Thousand Picture Comapre`");
            System.setProperty("apple.awt.application.appearance", "system");
            view.getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
        }
    }

    /**
     * Creates and shows the loading frame while modules are being loaded.
     * @param view the {@code View} instance used to show the loading frame
     * @param moduleLoader the {@code ModuleLoader} used to track loading progress
     * @return the {@code LoadingFrame} instance
     * @throws NullPointerException if {@code view} or {@code moduleLoader} is {@code null}
     */
    private static LoadingFrame createLoadingFrame(View view, ModuleLoader moduleLoader) {
        LoadingFrame lf = view.showLoadingFrame();
        moduleLoader.addPropertyChangeListener(lf);

        return lf;
    }

    /**
     * Loads all modules sequentially using the provided {@code ModuleLoader}.
     * @param view the {@code View} instance (used for displaying progress)
     * @param moduleLoader the {@code ModuleLoader} used to load modules
     * @throws IOException if an error occurs while loading modules
     * @throws NullPointerException if {@code view} or {@code moduleLoader} is {@code null}
     */
    private static void loadModules(View view, ModuleLoader moduleLoader) throws IOException {
        Objects.requireNonNull(view);
        Objects.requireNonNull(moduleLoader);

        while (moduleLoader.hasNext()) {
            moduleLoader.loadNext();
        }
    }

    /**
     * Initializes the {@code Controller} with the provided view, model, and locale.
     * @param view the {@code View} instance
     * @param model the {@code Model} instance
     * @param locale the {@code Locale} instance used for resource bundles
     * @return a new {@code Controller} instance
     * @throws NullPointerException if {@code view}, {@code model}, or {@code locale} is {@code null}
     */
    private static Controller initializeController(View view, Model model, Locale locale) {
        ResourceBundle varResources = ResourceBundle.getBundle("variables", locale);
        return new Controller(view, model, varResources);
    }

    /**
     * Launches the application by disposing of the loading frame and making the main view visible.
     * @param view the {@code View} instance to be made visible
     * @param loadingFrame the {@code LoadingFrame} to be disposed
     * @throws NullPointerException if {@code view} or {@code loadingFrame} is {@code null}
     */
    private static void launchApplication(View view, LoadingFrame loadingFrame) {
        Objects.requireNonNull(view);

        view.disposeLoadingFrame(loadingFrame);
        SwingUtilities.invokeLater(() -> view.setVisible(true));
    }

    /**
     * Handles errors by showing a dialog with the error message and exiting the application.
     * @param e the {@code Exception} that occurred
     * @throws NullPointerException if {@code e} is {@code null}
     */
    private static void handleError(Exception e) {
        Objects.requireNonNull(e);

        JOptionPane.showMessageDialog(
            null,
            "Could not start application.\nError message:\n" + e.getMessage(),
            "Error:",
            JOptionPane.ERROR_MESSAGE
        );

        System.exit(1);
    }
}
