package com.aheadshop.user.service;

import com.aheadshop.common.core.util.JwtUtil;
import com.aheadshop.common.redis.util.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CacheService cacheService;

    @Value("${jwt.access-token-ttl:7200000}")
    private long accessTokenTtl;

    @Value("${jwt.refresh-token-ttl:604800000}")
    private long refreshTokenTtl;

    private static final String TOKEN_PREFIX = "user:token:";
    private static final String REFRESH_PREFIX = "user:refresh:";

    public String[] generateTokenPair(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        String accessToken = JwtUtil.generateToken(String.valueOf(userId), accessTokenTtl, claims);
        String refreshToken = JwtUtil.generateToken(String.valueOf(userId), refreshTokenTtl, claims);

        cacheService.set(TOKEN_PREFIX + userId, accessToken, accessTokenTtl, TimeUnit.MILLISECONDS);
        cacheService.set(REFRESH_PREFIX + userId, refreshToken, refreshTokenTtl, TimeUnit.MILLISECONDS);

        return new String[]{accessToken, refreshToken};
    }

    public boolean validateToken(Long userId, String token) {
        Object cached = cacheService.get(TOKEN_PREFIX + userId);
        return token.equals(cached);
    }

    public void removeToken(Long userId) {
        cacheService.delete(TOKEN_PREFIX + userId);
        cacheService.delete(REFRESH_PREFIX + userId);
    }
}