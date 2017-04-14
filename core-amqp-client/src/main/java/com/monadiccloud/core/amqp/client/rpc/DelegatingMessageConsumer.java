package com.monadiccloud.core.amqp.client.rpc;

public interface DelegatingMessageConsumer {
    <S, D> void addAdapter(ServiceCallbackAdapter<S, D> callback);
}
