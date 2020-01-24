package io.dcbn.backend.graph.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

public class GraphLock {

    @Value("${graph.lock.expire.time}")
    private long timeToExpire;

    @Getter
    private long userId;
    private long expireTime;

    public GraphLock(long userId) {
            this.userId = userId;
            this.expireTime = System.currentTimeMillis() + timeToExpire;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}

