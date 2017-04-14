package com.monadiccloud.core.amqp.client.rpc;

public interface ServiceRequestCallback {
    String getRequestId();

    void executeRequest(String requestId) throws Exception;
}
