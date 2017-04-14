package com.monadiccloud.core.amqp.client.callback;

public class ServiceError {
    public String requestId = null;

    private String errorCode = null;

    private String errorMessage = null;

    public ServiceError(String requestId, String errorCode, String errorMessage) {
        super();
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public String toString() {
        return "ServiceError{" + "requestId='" + requestId + '\'' + ", errorCode='" + errorCode + '\'' + ", errorMessage='" + errorMessage
                + '\'' + '}';
    }
}
