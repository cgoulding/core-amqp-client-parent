package com.monadiccloud.core.amqp.client.callback;

public class ServiceTimeout {
    public String requestId = null;

    private long timeout = -1;

    public ServiceTimeout(String requestId, long timeout) {
        super();
        this.requestId = requestId;
        this.timeout = timeout;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public long getTimeout() {
        return this.timeout;
    }

    @Override
    public String toString() {
        return "ServiceTimeout{" + "requestId='" + requestId + '\'' + ", timeout=" + timeout + '}';
    }
}
