package com.monadiccloud.core.amqp.client.callback;

public class ServiceResponse<T> {
    public String requestId = null;

    private String message = null;

    private T response = null;

    public ServiceResponse(String requestId, T response, String message) {
        super();

        this.requestId = requestId;
        this.response = response;
        this.message = message;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public String getMessage() {
        return this.message;
    }

    public T getResponse() {
        return this.response;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" + "requestId='" + requestId + '\'' + ", message='" + message + '\'' + ", response=" + response + '}';
    }
}
