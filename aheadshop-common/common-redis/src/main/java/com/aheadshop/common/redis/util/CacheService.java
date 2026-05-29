package com.aheadshop.common.redis.util;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        return JSONUtil.toBean(json, clazz);
    }

    public <T> void setJson(String key, T value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), timeout, unit);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
}