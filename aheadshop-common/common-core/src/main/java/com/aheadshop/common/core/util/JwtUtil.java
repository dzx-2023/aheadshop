package com.aheadshop.common.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public final class JwtUtil {

    private JwtUtil() {}

    // 默认密钥（生产环境应从配置读取）
    private static final String SECRET = "aheadshop-plus-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * 生成 Token
     * @param subject   用户ID
     * @param ttlMillis 过期时间（毫秒）
     * @param claims    自定义声明
     */
    public static String generateToken(String subject, long ttlMillis, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMillis);

        var builder = Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(exp)
                .signWith(KEY);

        if (claims != null) {
            claims.forEach(builder::claim);
        }

        return builder.compact();
    }

    /**
     * 解析 Token
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}