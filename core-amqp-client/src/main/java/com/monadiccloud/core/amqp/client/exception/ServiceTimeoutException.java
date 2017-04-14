package com.monadiccloud.core.amqp.client.exception;

public final class ServiceTimeoutException extends Exception {
    public ServiceTimeoutException() {
        super();
    }

    public ServiceTimeoutException(Throwable cause) {
        super(cause);
    }

    public ServiceTimeoutException(String message) {
        super(message);
    }

    public ServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
