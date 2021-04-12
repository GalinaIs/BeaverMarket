package org.mycompany.service.util;

import java.util.concurrent.Executor;

public class ExecuteUtils {
    private ExecuteUtils() {
        throw new AssertionError("No instance!");
    }

    public static void execute(Executor executor, Runnable command) {
        if (executor == null) {
            command.run();
        } else {
            executor.execute(command);
        }
    }
}
