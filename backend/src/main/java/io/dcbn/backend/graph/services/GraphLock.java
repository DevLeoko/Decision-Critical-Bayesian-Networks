package io.dcbn.backend.graph.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

public class GraphLock {

    @Value("${graph.lock.expire.time}")
    private long timeToExpireInMillis;

    @Getter
    private long userId;
    private long expireTime;

    public GraphLock(long userId) {
        this.timeToExpireInMillis = 1000;

        this.userId = userId;
        this.expireTime = System.currentTimeMillis() + timeToExpireInMillis;

    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}

