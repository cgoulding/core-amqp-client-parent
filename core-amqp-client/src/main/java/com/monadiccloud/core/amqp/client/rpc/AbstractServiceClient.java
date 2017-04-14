package com.monadiccloud.core.amqp.client.rpc;

import com.monadiccloud.core.amqp.client.callback.DefaultServiceCallback;
import com.monadiccloud.core.amqp.client.callback.ServiceCallback;
import com.monadiccloud.core.amqp.client.callback.ServiceError;
import com.monadiccloud.core.amqp.client.callback.ServiceResponse;
import com.monadiccloud.core.amqp.client.exception.ServiceExecutionException;
import com.monadiccloud.core.amqp.client.exception.ServiceTimeoutException;
import com.monadiccloud.core.amqp.client.task.ServiceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public abstract class AbstractServiceClient extends AbstractServiceCallbackMonitor implements ServiceCallbackRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceClient.class);

    protected <REQ, RES extends ServiceResponse<?>> RES processRequest(long timeout, final ServiceRequestCallback serviceRequestCallback)
            throws ServiceTimeoutException, ServiceExecutionException {
        this.shutdownCheck();

        String requestId = serviceRequestCallback.getRequestId();
        if (requestId == null) {
            requestId = createRequestId();
        }

        final DefaultServiceCallback<RES> serviceCallback = new DefaultServiceCallback<>();

        this.createAndAddServiceTask(requestId, serviceCallback, timeout);

        try {
            serviceRequestCallback.executeRequest(requestId);
        } catch (Exception exception) {
            this.removeServiceTask(requestId);

            LOGGER.error(exception.getMessage(), exception);
            throw new ServiceExecutionException(exception.getMessage(), exception);
        }

        this.waitForServiceCallback(serviceCallback, requestId, timeout);
        this.checkForServiceError(serviceCallback);
        return serviceCallback.getServiceResponse();
    }

    @Override
    public void release() {
        super.release();
    }

    private void createAndAddServiceTask(final String requestId, final DefaultServiceCallback<?> callback, final long timeout) {
        ServiceTask<ServiceCallback<?>> task = new ServiceTask<>(requestId, callback, timeout);
        this.addServiceTask(requestId, task);
    }

    protected String createRequestId() {
        return uuid();
    }

    protected Date timestamp() {
        return Calendar.getInstance().getTime();
    }

    protected String uuid() {
        return UUID.randomUUID().toString();
    }

    private void checkForServiceError(final DefaultServiceCallback<?> callback) throws ServiceExecutionException {
        ServiceError error = callback.getServiceError();
        if (error != null) {
            throw new ServiceExecutionException(error.getErrorMessage());
        }
    }

    private void shutdownCheck() throws ServiceExecutionException {
        if (this.isShutDown()) {
            throw new ServiceExecutionException("Already shutdown");
        }
    }
}
