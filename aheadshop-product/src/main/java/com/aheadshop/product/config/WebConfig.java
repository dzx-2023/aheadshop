package com.aheadshop.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.product-dir:upload/product}")
    private String uploadDir;

    @Value("${upload.background-dir:D:/Intelli J IDEA/IDEA 项目/aheadshop-plus/upload/background}")
    private String backgroundDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/product/**")
                .addResourceLocations("file:" + uploadDir + "/");
        registry.addResourceHandler("/upload/background/**")
                .addResourceLocations("file:" + backgroundDir + "/");
    }
}
