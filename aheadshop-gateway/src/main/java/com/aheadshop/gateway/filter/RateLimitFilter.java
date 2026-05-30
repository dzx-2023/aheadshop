package com.aheadshop.gateway.filter;

import com.aheadshop.gateway.config.GatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final GatewayConfig gatewayConfig;

    /**
     * Lua 滑动窗口限流脚本
     * KEYS[1] = 限流 key
     * ARGV[1] = 窗口大小（毫秒）
     * ARGV[2] = 最大请求数
     * ARGV[3] = 当前时间戳（毫秒）
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
            "local window = tonumber(ARGV[1])\n" +
            "local limit = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local min = now - window\n" +
            "redis.call('ZREMRANGEBYSCORE', key, 0, min)\n" +
            "local count = redis.call('ZCARD', key)\n" +
            "if count < limit then\n" +
            "    redis.call('ZADD', key, now, now .. '-' .. math.random(100000))\n" +
            "    redis.call('EXPIRE', key, math.ceil(window / 1000))\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    private static final RedisScript<Long> SCRIPT = RedisScript.of(LUA_SCRIPT, Long.class);

    public RateLimitFilter(ReactiveStringRedisTemplate redisTemplate, GatewayConfig gatewayConfig) {
        this.redisTemplate = redisTemplate;
        this.gatewayConfig = gatewayConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();

        // 仅对非 GET 请求限流
        if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        String clientIp = getClientIp(exchange);
        String path = exchange.getRequest().getURI().getPath();
        String key = "rate_limit:" + clientIp + ":" + path;

        long now = System.currentTimeMillis();
        long windowMillis = gatewayConfig.getRateLimit().getWindow() * 1000L;

        return redisTemplate.execute(SCRIPT, List.of(key),
                        Arrays.asList(String.valueOf(windowMillis), String.valueOf(gatewayConfig.getRateLimit().getRate()), String.valueOf(now)))
                .next()
                .flatMap(result -> {
                    if (result != null && result == 1L) {
                        return chain.filter(exchange);
                    }
                    log.warn("限流触发: ip={}, path={}", clientIp, path);
                    return tooManyRequests(exchange);
                });
    }

    private String getClientIp(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            return ip.split(",")[0].trim();
        }
        ip = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty()) {
            return ip;
        }
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    private Mono<Void> tooManyRequests(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":429,\"msg\":\"请求过于频繁，请稍后重试\",\"data\":null}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -50;
    }
}