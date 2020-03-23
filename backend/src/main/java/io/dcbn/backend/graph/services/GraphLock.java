package io.dcbn.backend.graph.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

public class GraphLock {

    @Getter
    private long userId;
    private long expireTime;

    public GraphLock(long userId, long timeToExpireInMillis) {
        this.userId = userId;
        this.expireTime = System.currentTimeMillis() + timeToExpireInMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}

