package com.aheadshop.aichat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.aheadshop")
@EnableDiscoveryClient
@EnableFeignClients
public class AichatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AichatApplication.class, args);
    }
}
