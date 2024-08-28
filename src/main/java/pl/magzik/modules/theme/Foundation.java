package pl.magzik.modules.theme;

import com.formdev.flatlaf.util.SystemInfo;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Interface for the Foundation framework, which provides access to macOS API functions
 * related to Objective-C runtime operations.
 * <p>
 * This interface provides methods for interacting with the Objective-C runtime, including
 * retrieving classes, sending messages to objects, and registering selectors.
 * It extends {@link Library} to use JNA's capabilities for loading and interfacing with native libraries.
 * </p>
 */
public interface Foundation extends Library {
    /**
     * The singleton instance of the {@code Foundation} library.
     * <p>
     * This instance is created only if the operating system is macOS. If the system is not macOS,
     * the instance will be {@code null}.
     * </p>
     */
    Foundation INSTANCE = SystemInfo.isMacOS ? Native.load("Foundation", Foundation.class) : null;

    /**
     * Retrieves a class object by its name.
     * <p>
     * This function retrieves the class object corresponding to the specified class name.
     * </p>
     *
     * @param className the name of the class to retrieve
     * @return a {@link Pointer} to the class object, or {@code null} if the class could not be found
     */
    Pointer NSClassFromString(String className);

    /**
     * Sends a message to an Objective-C object.
     * <p>
     * This function sends a message (method call) to the specified Objective-C object.
     * </p>
     *
     * @param receiver the object to which the message is sent
     * @param selector the selector (method) to call
     * @param args optional arguments for the method
     * @return a {@link Pointer} to the result of the method call, or {@code null} if the call failed
     */
    Pointer objc_msgSend(Pointer receiver, Pointer selector, Object... args);

    /**
     * Registers a selector name.
     * <p>
     * This function registers a selector name with the Objective-C runtime and returns a pointer
     * to the registered selector.
     * </p>
     *
     * @param selectorName the name of the selector to register
     * @return a {@link Pointer} to the registered selector
     */
    Pointer sel_registerName(String selectorName);
}
