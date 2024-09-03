package pl.magzik.async;

import pl.magzik.base.interfaces.Command;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface AsyncTaskFactory {

    ExecutorService executor = ExecutorServiceManager.getInstance().getExecutorService();

    default CompletableFuture<Void> execute(Command...commands) {
        CompletableFuture<Void> ftr = CompletableFuture.completedFuture(null);

        for (Command command : commands) {
            ftr = ftr.thenRunAsync(command::execute, executor);
        }

        return ftr;
    }
}
