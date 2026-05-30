package com.aheadshop.gateway.config;

import com.aheadshop.gateway.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtInitConfig {

    @Value("${gateway.jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        JwtUtil.init(secret);
    }
}