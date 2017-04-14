package com.monadiccloud.core.amqp.client.rpc;

import com.monadiccloud.core.amqp.client.callback.ServiceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMessageConsumer implements DelegatingMessageConsumer {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractMessageConsumer.class);
    private Map<Class, ServiceCallbackAdapter> adapters = new HashMap<>();

    @Override
    public <S, D> void addAdapter(ServiceCallbackAdapter<S, D> callback) {
        adapters.put(callback.getSourceClass(), callback);
    }

    public void handleMessage(Object message) {
        if (message != null) {
            ServiceCallbackAdapter adapter = adapters.get(message.getClass());
            if (adapter != null) {
                handleResponse(message, adapter);
            }
        }
    }

    protected <M, R> void handleResponse(final M message, final ServiceCallbackAdapter<M, R> handler) {
        if (message == null) {
            return;
        }

        final ServiceCallback callback = handler.take(message);

        if (callback == null) {
            return;
        }

        final R response = handler.transform(message);

        try {
            handler.consume(callback, response);
        } catch (Exception exception) {
            LOGGER.error("Error handling response", exception);
        }
    }
}
