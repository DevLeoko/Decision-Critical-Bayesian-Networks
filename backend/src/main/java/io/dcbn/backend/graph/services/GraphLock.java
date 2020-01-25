package io.dcbn.backend.graph.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

public class GraphLock {
    @Getter
    private long userId;
    @Getter
    private long expireTime;

    @Value("${graph.lock.expire.time}")
    private long sessionLimit;


    public GraphLock(long user) {
            this.userId=user;
            this.expireTime=System.currentTimeMillis()+sessionLimit;
    }
}

