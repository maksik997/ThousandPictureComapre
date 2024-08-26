package pl.magzik.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages a single instance of an {@link ExecutorService} using the Singleton pattern, ensuring
 * that there is only one instance of the executor throughout the application.
 * <p>
 * This class provides a centralized management of an {@code ExecutorService}, which is used to execute
 * tasks concurrently. It also ensures that the executor is properly shut down when the JVM is exiting
 * by registering a shutdown hook.
 * </p>
 * <p>
 * The {@code ExecutorServiceManager} is thread-safe and lazy-initialized, meaning that the instance
 * of this class is created only when it is first requested.
 * </p>
 */
public class ExecutorServiceManager {

    private final ExecutorService executorService;

    /**
     * Private constructor to prevent external instantiation.
     * <p>
     * Initializes the {@code ExecutorService} and registers a shutdown hook
     * to ensure it is properly terminated when the JVM shuts down.
     * </p>
     */
    private ExecutorServiceManager() {
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        addExecutorShutdownHook();
    }

    /**
     * Inner static class responsible for holding the singleton instance of {@code ExecutorServiceManager}.
     * <p>
     * This implementation leverages the class loader mechanism to ensure that the instance is
     * created only when it is first accessed, and it is also thread-safe.
     * </p>
     */
    private static final class InstanceHolder {
        private static final ExecutorServiceManager instance = new ExecutorServiceManager();
    }

    /**
     * Returns the singleton instance of the {@code ExecutorServiceManager}.
     * <p>
     * This method provides a global point of access to the single instance of {@code ExecutorServiceManager}.
     * It uses the {@code InstanceHolder} class to ensure that the instance is created in a thread-safe manner
     * and only when needed.
     * </p>
     *
     * @return the singleton instance of {@code ExecutorServiceManager}.
     */
    public static ExecutorServiceManager getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Returns the managed {@code ExecutorService}.
     * <p>
     * The {@code ExecutorService} is used to execute tasks concurrently. The instance returned
     * by this method is the same throughout the lifecycle of the application, as it is managed
     * by the singleton {@code ExecutorServiceManager}.
     * </p>
     *
     * @return the managed {@code ExecutorService}.
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Registers a shutdown hook to properly shut down the {@code ExecutorService} when the JVM is shutting down.
     * <p>
     * This method adds a shutdown hook that gracefully shuts down the {@code executorService} by first attempting
     * to stop accepting new tasks and completing existing ones.
     * If the executorService does not terminate within
     * 60 seconds,
     * the shutdown process is forced by canceling active tasks and preventing waiting tasks from starting.
     * <p>
     * The method also handles {@link InterruptedException} by immediately forcing the shutdown and restoring
     * the interrupted status of the current thread.
     * </p>
     * <p>
     * The shutdown hook ensures that the executorService is properly terminated before the JVM exits, preventing
     * potential resource leaks or unfinished tasks.
     * </p>
     *
     * @see ExecutorService#shutdown()
     * @see ExecutorService#shutdownNow()
     * @see ExecutorService#awaitTermination(long, TimeUnit)
     */
    private void addExecutorShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        System.err.println("executorService did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }
}
