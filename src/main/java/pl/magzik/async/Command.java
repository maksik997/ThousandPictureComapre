package pl.magzik.async;

/**
 * Represents a command that can be executed.
 * <p>
 * This functional interface allows for the definition of a single method, {@code execute()},
 * which encapsulates a specific operation or action. It is used in various scenarios where
 * commands need to be executed, such as in command pattern implementations or when tasks
 * are submitted for execution.
 * </p>
 * <p>
 * Implementations of this interface can be used to define and execute specific actions, either
 * directly or indirectly through higher-level abstractions. Since this is a functional interface,
 * it can be instantiated using lambda expressions or method references.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 *  Command command = () -> System.out.println("Executing command");
 *  command.execute(); // Outputs: Executing command
 *  }
 * </pre>
 * </p>
 */
@FunctionalInterface
public interface Command {

    /**
     * Executes the command.
     * <p>
     * This method contains the logic of the command and is invoked to perform the associated
     * action. The specific implementation of this method will define what the command does
     * when executed.
     * </p>
     */
    void execute();
}
