package pl.magzik.modules.theme;

import com.formdev.flatlaf.util.SystemInfo;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * A utility class for detecting the current system theme (dark or light) based on the operating system.
 * <p>
 * This class supports detecting the theme on Windows system. It utilizes platform-specific
 * API calls to determine the theme and provides a method to get the system theme.
 * </p>
 */
public class ThemeDetector {
    /**
     * Gets the current system theme.
     * <p>
     * Determines the system theme based on the operating system:
     * - Windows: Checks if the dark theme is enabled.
     * - Unknown: Returns {@code false} if the operating system is not recognized.
     * </p>
     *
     * @return {@code true} if the dark theme is active, {@code false} otherwise
     */
    public static boolean isDarkTheme() {
        return getOs().equals("windows") && isDarkWindowsTheme();
    }

    /**
     * Retrieves the name of the operating system.
     * <p>
     * Returns a string indicating the operating system:
     * - "windows" if the system is Windows
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

}
