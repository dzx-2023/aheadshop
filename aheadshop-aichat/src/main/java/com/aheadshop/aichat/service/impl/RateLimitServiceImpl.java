package com.aheadshop.aichat.service.impl;

import com.aheadshop.aichat.service.RateLimitService;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 基于 Redis 的滑动窗口限流实现
 *
 * <ul>
 *   <li>单用户每分钟最多 20 条消息</li>
 *   <li>单用户每日最多 200 条消息</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final String RATE_MINUTE_KEY = "chat:rate:%d:min";
    private static final String RATE_DAY_KEY = "chat:rate:%d:day";
    private static final int MAX_PER_MINUTE = 20;
    private static final int MAX_PER_DAY = 200;
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void checkRateLimit(Long userId) {
        // 每分钟限流
        String minuteKey = String.format(RATE_MINUTE_KEY, userId);
        Long minuteCount = redisTemplate.opsForValue().increment(minuteKey);
        if (minuteCount != null && minuteCount == 1) {
            // 首次设置，给 60 秒过期
            redisTemplate.expire(minuteKey, Duration.ofSeconds(60));
        }
        if (minuteCount != null && minuteCount > MAX_PER_MINUTE) {
            log.warn("用户 {} 每分钟限流: count={}", userId, minuteCount);
            throw new BusinessException(BusinessExceptionCode.CHAT_RATE_LIMIT,
                    "消息发送太频繁，请稍后再试");
        }

        // 每日限流
        String today = LocalDate.now().format(DAY_FMT);
        String dayKey = String.format(RATE_DAY_KEY, userId) + ":" + today;
        Long dayCount = redisTemplate.opsForValue().increment(dayKey);
        if (dayCount != null && dayCount == 1) {
            // 首次设置，到当天结束过期
            redisTemplate.expire(dayKey, Duration.ofHours(24));
        }
        if (dayCount != null && dayCount > MAX_PER_DAY) {
            log.warn("用户 {} 每日限流: count={}", userId, dayCount);
            throw new BusinessException(BusinessExceptionCode.CHAT_DAILY_LIMIT,
                    "您今天的对话次数已用完，请明天再来");
        }

        log.debug("限流检查通过: userId={}, min={}, day={}", userId, minuteCount, dayCount);
    }
}
