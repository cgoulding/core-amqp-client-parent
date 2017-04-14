package com.monadiccloud.core.amqp.client.task;

public interface TimeoutTaskMonitor {
    void checkForTimedoutTasks();
}
