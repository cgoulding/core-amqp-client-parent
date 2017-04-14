package com.monadiccloud.core.amqp.client.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeoutTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutTask.class);
    private TimeoutTaskMonitor manager = null;

    public TimeoutTask(TimeoutTaskMonitor manager) {
        super();

        if (manager == null) {
            throw new IllegalArgumentException("The timeout task manager is null.");
        }

        this.manager = manager;
    }

    public void run() {
        try {
            this.manager.checkForTimedoutTasks();
        } catch (Exception exception) {
            LOGGER.error("TimeoutTask error", exception);
        }
    }
}
