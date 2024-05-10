package com.example.connection_server.baiduAI;

import java.time.Instant;

//限制每秒请求
public class RequestLimiter {
    private int requestLimit; // 每秒请求数量的限制
    private int requestsCount; // 当前秒已发送的请求数量
    private Instant lastResetTime; // 上次重置计数器的时间点

    public RequestLimiter(int requestLimit) {
        this.requestLimit = requestLimit;
        this.requestsCount = 0;
        this.lastResetTime = Instant.now();
    }

    public synchronized boolean canSendRequest() {
        Instant now = Instant.now();
        // 如果当前时间与上次重置计数器的时间点不在同一秒内，则重置计数器
        if (now.getEpochSecond() != lastResetTime.getEpochSecond()) {
            requestsCount = 0;
            lastResetTime = now;
        }
        // 检查当前秒已发送的请求数量是否达到限制
        if (requestsCount < requestLimit) {
            requestsCount++;
            return true;
        } else {
            return false;
        }
    }
}

