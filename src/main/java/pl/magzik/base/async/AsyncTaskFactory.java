package pl.magzik.base.async;

import pl.magzik.base.interfaces.Command;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * An interface for asynchronously executing a sequence of commands using an {@link ExecutorService}.
 * It provides a default method to chain execution of multiple commands asynchronously.
 * <p>
 * The commands are executed in the order they are provided, each asynchronously after the previous one completes.
 */
public interface AsyncTaskFactory {

    /**
     * The {@link ExecutorService} used to execute commands asynchronously.
     * It is obtained from the {@link ExecutorServiceManager} singleton instance.
     */
    ExecutorService executor = ExecutorServiceManager.getInstance().getExecutorService();

    /**
     * Executes a sequence of {@link Command} objects asynchronously.
     * <p>
     * Each command is executed in sequence after the previous one completes, using the provided {@link ExecutorService}.
     * The method returns a {@link CompletableFuture} that is completed when all commands have been executed.
     *
     * @param commands The sequence of {@link Command} objects to be executed asynchronously.
     * @return A {@link CompletableFuture} that completes when all commands have been executed.
     */
    default CompletableFuture<Void> execute(Command...commands) {
        CompletableFuture<Void> ftr = CompletableFuture.completedFuture(null);

        for (Command command : commands) {
            ftr = ftr.thenRunAsync(command::execute, executor);
        }

        return ftr;
    }
}
