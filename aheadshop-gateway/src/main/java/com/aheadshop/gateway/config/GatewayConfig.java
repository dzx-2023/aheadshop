package com.aheadshop.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayConfig {

    private Jwt jwt = new Jwt();
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class Jwt {
        private String secret;
        private List<String> whitelist = new ArrayList<>();
    }

    @Data
    public static class RateLimit {
        private int rate = 50;
        private int window = 1;
    }
}
