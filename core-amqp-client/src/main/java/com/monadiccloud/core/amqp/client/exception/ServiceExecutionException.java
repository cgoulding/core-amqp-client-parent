package com.monadiccloud.core.amqp.client.exception;

public final class ServiceExecutionException extends Exception {
    public ServiceExecutionException() {
        super();
    }

    public ServiceExecutionException(Throwable cause) {
        super(cause);
    }

    public ServiceExecutionException(String message) {
        super(message);
    }

    public ServiceExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
