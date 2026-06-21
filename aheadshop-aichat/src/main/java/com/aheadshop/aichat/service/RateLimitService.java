package com.aheadshop.aichat.service;

/**
 * 用户限流服务
 * 基于 Redis 滑动窗口限流
 */
public interface RateLimitService {

    /**
     * 检查用户是否超出限流，超限则抛出异常
     * @param userId 用户ID
     */
    void checkRateLimit(Long userId);
}
