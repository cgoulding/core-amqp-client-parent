package com.monadiccloud.core.amqp.client.callback;

public class DefaultServiceCallback<T extends ServiceResponse<?>> implements ServiceCallback<T> {
    private volatile boolean done = false;
    private ServiceError error = null;
    private T serviceResponse = null;

    public DefaultServiceCallback() {
        super();
    }

    public ServiceError getServiceError() {
        return this.error;
    }

    public T getServiceResponse() {
        return this.serviceResponse;
    }

    public boolean isDone() {
        return this.done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }

    @Override
    public void handleServiceError(ServiceError error) {
        this.error = error;

        this.setDone(true);
    }

    @Override
    public void handleServiceTimeout(ServiceTimeout timeout) {
        // do nothing
    }

    @Override
    public void handleServiceResponse(final T serviceResponse) {
        this.serviceResponse = serviceResponse;

        this.setDone(true);
    }
}
