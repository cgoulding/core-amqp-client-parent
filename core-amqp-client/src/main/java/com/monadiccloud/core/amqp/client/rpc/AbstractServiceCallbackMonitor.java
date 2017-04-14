package com.monadiccloud.core.amqp.client.rpc;

import com.monadiccloud.core.amqp.client.callback.DefaultServiceCallback;
import com.monadiccloud.core.amqp.client.callback.ServiceCallback;
import com.monadiccloud.core.amqp.client.callback.ServiceTimeout;
import com.monadiccloud.core.amqp.client.exception.ServiceTimeoutException;
import com.monadiccloud.core.amqp.client.task.ServiceTask;
import com.monadiccloud.core.amqp.client.task.TimeoutTask;
import com.monadiccloud.core.amqp.client.task.TimeoutTaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractServiceCallbackMonitor implements TimeoutTaskMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceCallbackMonitor.class);

    private volatile boolean shutdown = false;
    private Map<String, ServiceTask<ServiceCallback<?>>> requests = null;
    private ScheduledExecutorService executorService = null;

    public AbstractServiceCallbackMonitor() {
        super();
        this.requests = new HashMap<>();
        this.executorService = this.makeScheduledExecutorService(1000l, 1000l, TimeUnit.MILLISECONDS);
    }

    public boolean isShutDown() {
        return this.shutdown;
    }

    public void addServiceTask(final String requestId, final ServiceTask<ServiceCallback<?>> serviceTask) {
        if (requestId == null) {
            return;
        }

        if (serviceTask == null) {
            return;
        }

        synchronized (this.requests) {
            this.requests.put(requestId, serviceTask);
        }
    }

    public ServiceTask<ServiceCallback<?>> removeServiceTask(final String requestId) {
        synchronized (this.requests) {
            return this.requests.remove(requestId);
        }
    }

    public ServiceTask<ServiceCallback<?>> getServiceTask(final String requestId) {
        synchronized (this.requests) {
            return this.requests.get(requestId);
        }
    }

    public ServiceCallback<?> removeServiceCallback(final String requestId) {
        if (requestId == null) {
            return null;
        }

        ServiceTask<ServiceCallback<?>> task = null;

        synchronized (this.requests) {
            task = this.requests.remove(requestId);
        }

        if (task == null) {
            return null;
        }

        return task.getServiceCallback();
    }

    public boolean cancel(final String requestId) {
        if (requestId == null) {
            return false;
        }

        ServiceTask<ServiceCallback<?>> task = null;

        synchronized (this.requests) {
            task = this.requests.remove(requestId);
        }

        return (task != null);
    }

    public void release() {
        if (this.shutdown) {
            return;
        }

        this.shutdown = true;

        LOGGER.info("Shutting down");
        this.shutdown(this.executorService);

        LOGGER.info("Waiting for requests to clear down");
        this.waitForRequests(10000l);
    }

    @Override
    public void checkForTimedoutTasks() {
        final List<ServiceTask<ServiceCallback<?>>> timedOutTasks = new ArrayList<ServiceTask<ServiceCallback<?>>>();

        String[] keyArray = null;

        synchronized (this.requests) {
            final Set<String> keySet = this.requests.keySet();

            keyArray = keySet.toArray(new String[keySet.size()]);
        }

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < keyArray.length; i++) {
            final String requestId = keyArray[i];

            ServiceTask<ServiceCallback<?>> task = this.requests.get(requestId);

            if (task == null) {
                continue;
            }

            if (task.hasTimedout(currentTime)) {
                synchronized (this.requests) {
                    task = this.requests.remove(requestId);

                    if (task != null) {
                        timedOutTasks.add(task);
                    }
                }
            }
        }

        for (int i = 0; i < timedOutTasks.size(); i++) {
            final ServiceTask<ServiceCallback<?>> task = timedOutTasks.get(i);

            final ServiceCallback<?> callback = task.getServiceCallback();

            final ServiceTimeout timeout = new ServiceTimeout(task.getRequestId(), task.getTimeout());

            try {
                callback.handleServiceTimeout(timeout);
            } catch (Exception exception) {
                LOGGER.error("Error handling timeout", exception);
            }
        }
    }

    protected ScheduledExecutorService makeScheduledExecutorService(long initialDelay, long delay, TimeUnit timeUnit) {
        TimeoutTask timeoutTask = new TimeoutTask(this);

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleWithFixedDelay(timeoutTask, initialDelay, delay, TimeUnit.MILLISECONDS);

        return executorService;
    }

    protected boolean waitForRequests(long timeout) {
        if ((this.requests == null) || (this.requests.size() == 0)) {
            return true;
        }

        long timeLimit = timeout;
        long sleepTime = 250l;
        long elapsedTime = 0;

        // wait for the requests to complete and clear down
        while (this.requests.size() > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException exception) {
            }

            elapsedTime += sleepTime;

            if (elapsedTime >= timeLimit) {
                // clear the requests
                this.requests.clear();

                return false;
            }
        }

        return true;
    }

    protected void shutdown(final ExecutorService executorService) {
        if (executorService == null) {
            return;
        }

        // stop new tasks from being submitted
        executorService.shutdown();

        try {
            boolean graceful = executorService.awaitTermination(3, TimeUnit.SECONDS);

            // allow pending tasks to finish
            if (graceful == false) {
                // cancel currently executing tasks
                executorService.shutdownNow();

                executorService.awaitTermination(3, TimeUnit.SECONDS);
            }

        } catch (InterruptedException ie) {
            // (re-)cancel if current thread also interrupted
            executorService.shutdownNow();

            // preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    protected void waitForServiceCallback(final DefaultServiceCallback<?> serviceCallback, final String requestId, long timeout)
            throws ServiceTimeoutException {
        if (serviceCallback == null) {
            return;
        }

        // wait from the response from the service
        long timeLimit = timeout;
        long sleepTime = 10l;
        long elapsedTime = 0;

        // the callback is done if a response or error is handled by the manager
        while (serviceCallback.isDone() == false) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException exception) {
            }

            // if the timeout is greater than zero then check for elapsed time
            if (timeLimit > 0l) {
                elapsedTime += sleepTime;

                if (elapsedTime >= timeLimit) {
                    synchronized (this.requests) {
                        this.requests.remove(requestId);
                    }

                    LOGGER.error("Error waiting for service callback");
                    throw new ServiceTimeoutException("Error waiting for service callback");
                }
            }
        }
    }
}