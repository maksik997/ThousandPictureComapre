package pl.magzik.modules.theme;

import com.formdev.flatlaf.util.SystemInfo;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * A utility class for detecting the current system theme (dark or light) based on the operating system.
 * <p>
 * This class supports detecting the theme on Windows and macOS systems. It utilizes platform-specific
 * API calls to determine the theme and provides a method to get the system theme.
 * </p>
 */
public class ThemeDetector {
    public static void main(String[] args) {
        System.out.println(isDarkTheme());
    }

    /**
     * Gets the current system theme.
     * <p>
     * Determines the system theme based on the operating system:
     * - Windows: Checks if the dark theme is enabled.
     * - macOS: Checks if the dark theme is enabled.
     * - Unknown: Returns {@code false} if the operating system is not recognized.
     * </p>
     *
     * @return {@code true} if the dark theme is active, {@code false} otherwise
     */
    public static boolean isDarkTheme() {
        return switch (getOs()) {
            case "windows" -> isDarkWindowsTheme();
            case "mac" -> isMacDarkTheme();
            default -> false;
        };
    }

    /**
     * Retrieves the name of the operating system.
     * <p>
     * Returns a string indicating the operating system:
     * - "windows" if the system is Windows
     * - "mac" if the system is macOS
     * - "unknown" if the system is neither Windows nor macOS
     * </p>
     *
     * @return the name of the operating system
     */
    private static String getOs(){
        if (SystemInfo.isWindows)
            return "windows";
        else if (SystemInfo.isMacOS)
            return "mac";

        return "unknown";
    }

    /**
     * Checks if the dark theme is enabled on Windows.
     * <p>
     * Queries the Windows Registry to determine if the dark theme is active.
     * </p>
     *
     * @return {@code true} if the dark theme is enabled, {@code false} otherwise
     */
    private static boolean isDarkWindowsTheme() {
        final Pointer HKEY_CURRENT_USER = new Pointer(0x80000001L);
        final int KEY_READ = 0x20019;
        final int REG_DWORD = 4;
        final String subKey = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize";
        final String valueName = "AppsUseLightTheme";

        IntByReference pHKey = new IntByReference();
        if (Advapi32.INSTANCE.RegOpenKeyEx(HKEY_CURRENT_USER, subKey, 0, KEY_READ, pHKey) != 0) {
            return false;
        }

        Pointer hKeyPointer = new Pointer(pHKey.getValue());
        IntByReference dataType = new IntByReference();
        IntByReference data = new IntByReference();
        IntByReference dataSize = new IntByReference(4);

        if (Advapi32.INSTANCE.RegQueryValueEx(hKeyPointer, valueName, null, dataType, data, dataSize) != 0 || dataType.getValue() != REG_DWORD) {
            Advapi32.INSTANCE.RegCloseKey(hKeyPointer);
            return false;
        }

        Advapi32.INSTANCE.RegCloseKey(hKeyPointer);

        return data.getValue() == 0;
    }

    /**
     * Checks if the dark theme is enabled on macOS.
     * <p>
     * Queries the macOS system defaults to determine if the dark theme is active.
     * </p>
     *
     * @return {@code true} if the dark theme is enabled, {@code false} otherwise
     */
    private static boolean isMacDarkTheme() {
        Foundation foundation = Foundation.INSTANCE;

        Pointer NSAutoreleasePool = foundation.NSClassFromString("NSAutoreleasePool");
        Pointer alloc = foundation.sel_registerName("alloc");
        Pointer init = foundation.sel_registerName("init");
        Pointer pool = foundation.objc_msgSend(foundation.objc_msgSend(NSAutoreleasePool, alloc), init);

        try {
            Pointer NSUserDefaults = foundation.NSClassFromString("NSUserDefaults");
            Pointer standardUserDefaults = foundation.sel_registerName("standardUserDefaults");
            Pointer defaults = foundation.objc_msgSend(NSUserDefaults, standardUserDefaults);

            Pointer NSString = foundation.NSClassFromString("NSString");
            Pointer allocString = foundation.sel_registerName("alloc");
            Pointer initWithUTF8String = foundation.sel_registerName("initWithUTF8String:");

            Pointer key = foundation.objc_msgSend(foundation.objc_msgSend(NSString, allocString), initWithUTF8String, "AppleInterfaceStyle");

            Pointer stringForKey = foundation.sel_registerName("stringForKey:");
            Pointer appearance = foundation.objc_msgSend(defaults, stringForKey, key);

            String appearanceStr = appearance != null ? foundation.objc_msgSend(appearance, foundation.sel_registerName("UTF8String")).getString(0) : "";
            return "Dark".equals(appearanceStr);
        } finally {
            foundation.objc_msgSend(pool, foundation.sel_registerName("release"));
        }
    }
}
