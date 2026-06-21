package com.aheadshop.aichat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class LlmConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) throws IOException {
        ClassPathResource resource = new ClassPathResource("prompts/system-prompt.st");
        String defaultSystemPrompt = resource.getContentAsString(StandardCharsets.UTF_8);

        return ChatClient.builder(chatModel)
                .defaultSystem(defaultSystemPrompt)
                .build();
    }
}
