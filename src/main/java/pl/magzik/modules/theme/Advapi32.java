package pl.magzik.modules.theme;

import com.formdev.flatlaf.util.SystemInfo;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Interface for the Advapi32 library, which provides access to Windows API functions
 * related to the Windows Registry.
 * <p>
 * This interface provides methods for opening, closing, and querying Windows Registry keys.
 * It extends {@link StdCallLibrary} to use the standard call convention for Windows API functions.
 * </p>
 */
public interface Advapi32 extends StdCallLibrary {
    /**
     * The singleton instance of the {@code Advapi32} library.
     */
    Advapi32 INSTANCE = SystemInfo.isWindows ? Native.load("Advapi32", Advapi32.class, W32APIOptions.DEFAULT_OPTIONS) : null;

    /**
     * Opens the specified registry key.
     * <p>
     * This function opens a specified registry key and returns a handle to the key. The handle
     * can be used in other registry functions to access or modify the key.
     * </p>
     *
     * @param hKey the handle to an open registry key, or a predefined key constant (e.g., {@code HKEY_LOCAL_MACHINE})
     * @param lpSubKey the name of the subkey to open
     * @param ulOptions reserved; must be zero
     * @param samDesired the desired access rights for the key
     * @param phkResult a reference to a variable that receives the handle of the opened key
     * @return {@code ERROR_SUCCESS} if the function succeeds, otherwise an error code
     */
    int RegOpenKeyEx(Pointer hKey, String lpSubKey, int ulOptions, int samDesired, IntByReference phkResult);

    /**
     * Closes an open registry key.
     * <p>
     * This function closes a registry key handle previously opened by {@link #RegOpenKeyEx}.
     * </p>
     *
     * @param hKey the handle to the open registry key
     * @return {@code ERROR_SUCCESS} if the function succeeds, otherwise an error code
     */
    int RegCloseKey(Pointer hKey);

    /**
     * Queries the value of a specified registry key.
     * <p>
     * This function retrieves the type and data for a specified value name associated thenLoad a registry key.
     * </p>
     *
     * @param hKey the handle to the open registry key
     * @param lpValueName the name of the value to query
     * @param lpReserved reserved; must be {@code null}
     * @param lpType a reference to a variable that receives the type of the value
     * @param lpData a reference to a buffer that receives the value's data
     * @param lpcbData a reference to a variable that specifies the size of the buffer
     * @return {@code ERROR_SUCCESS} if the function succeeds, otherwise an error code
     */
    int RegQueryValueEx(Pointer hKey, String lpValueName, int[] lpReserved, IntByReference lpType, IntByReference lpData, IntByReference lpcbData);
}