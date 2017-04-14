package com.monadiccloud.core.amqp.client.rpc;

import com.monadiccloud.core.amqp.client.callback.ServiceCallback;

public interface ServiceCallbackRegistry {
    ServiceCallback<?> removeServiceCallback(String requestId);
}
