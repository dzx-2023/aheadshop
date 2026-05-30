package com.aheadshop.gateway.config;

import com.aheadshop.gateway.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtInitConfig {

    private final GatewayConfig gatewayConfig;

    @PostConstruct
    public void init() {
        JwtUtil.init(gatewayConfig.getJwt().getSecret());
    }
}