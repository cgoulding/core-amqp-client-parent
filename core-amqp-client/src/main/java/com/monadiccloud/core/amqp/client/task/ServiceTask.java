package com.monadiccloud.core.amqp.client.task;

public class ServiceTask<T> {
    public String requestId = null;
    private T callback = null;
    private long timestamp = -1;
    private long timeout = -1;

    public ServiceTask(String requestId, T callback, long timeout) {
        super();

        this.requestId = requestId;
        this.callback = callback;
        this.timeout = timeout;
        this.timestamp = System.currentTimeMillis();
    }

    public String getRequestId() {
        return this.requestId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public T getServiceCallback() {
        return this.callback;
    }

    public boolean hasTimedout(long currentTime) {
        if (this.timeout <= 0) {
            return false;
        }

        long elapsedTime = currentTime - this.timestamp;
        return (elapsedTime >= this.timeout);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ServiceTask<?> that = (ServiceTask<?>) o;

        if (timestamp != that.timestamp)
            return false;
        if (timeout != that.timeout)
            return false;
        if (callback != null ? !callback.equals(that.callback) : that.callback != null)
            return false;
        return requestId != null ? requestId.equals(that.requestId) : that.requestId == null;

    }

    @Override
    public int hashCode() {
        int result = callback != null ? callback.hashCode() : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (int) (timeout ^ (timeout >>> 32));
        result = 31 * result + (requestId != null ? requestId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServiceTask{" + "callback=" + callback + ", timestamp=" + timestamp + ", timeout=" + timeout + ", requestId='" + requestId
                + '\'' + '}';
    }
}
