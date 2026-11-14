package com.mycompany.sistemadegestaoeagendamentodesalas.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingUtilities;

public final class DatabaseTaskExecutor {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "sgsas-database-worker");
        thread.setDaemon(true);
        return thread;
    });

    private DatabaseTaskExecutor() {
    }

    public static boolean submit(Runnable operation, Runnable onSuccess,
            Consumer<RuntimeException> onFailure) {
        return submit(() -> {
            operation.run();
            return null;
        }, ignored -> onSuccess.run(), onFailure);
    }

    public static <T> boolean submit(Supplier<T> operation, Consumer<T> onSuccess,
            Consumer<RuntimeException> onFailure) {
        try {
            EXECUTOR.execute(() -> {
                try {
                    T result = operation.get();
                    SwingUtilities.invokeLater(() -> onSuccess.accept(result));
                } catch (RuntimeException exception) {
                    SwingUtilities.invokeLater(() -> onFailure.accept(exception));
                }
            });
            return true;
        } catch (RejectedExecutionException exception) {
            SwingUtilities.invokeLater(() -> onFailure.accept(exception));
            return false;
        }
    }
}
