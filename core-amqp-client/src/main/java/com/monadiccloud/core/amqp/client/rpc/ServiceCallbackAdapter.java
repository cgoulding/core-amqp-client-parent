package com.monadiccloud.core.amqp.client.rpc;

import com.monadiccloud.core.amqp.client.callback.ServiceCallback;

public interface ServiceCallbackAdapter<S, D> {
    D transform(S source);

    void consume(ServiceCallback callback, D destination);

    ServiceCallback take(S source);

    Class<S> getSourceClass();
}
