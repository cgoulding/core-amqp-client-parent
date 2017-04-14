package com.monadiccloud.core.amqp.client.callback;

public interface ServiceCallback<T extends ServiceResponse<?>> {
    void handleServiceError(ServiceError error);

    void handleServiceTimeout(ServiceTimeout timeout);

    void handleServiceResponse(T serviceResponse);
}
