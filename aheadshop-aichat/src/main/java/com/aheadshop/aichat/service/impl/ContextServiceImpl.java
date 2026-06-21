package com.aheadshop.aichat.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aheadshop.aichat.service.ContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContextServiceImpl implements ContextService {

    private static final String CONTEXT_KEY_PREFIX = "chat:context:";
    private static final int MAX_CONTEXT_MESSAGES = 20;
    private static final long CONTEXT_TTL_HOURS = 24;

    private final StringRedisTemplate redisTemplate;

    @Override
    public List<Message> buildContext(Long userId, Long sessionId, String userMessage) {
        String key = CONTEXT_KEY_PREFIX + sessionId;
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, MAX_CONTEXT_MESSAGES - 1);

        if (jsonList == null || jsonList.isEmpty()) {
            return Collections.emptyList();
        }

        // Redis LPUSH 存的顺序是最新在前，需要反转为时间正序
        List<String> reversed = new ArrayList<>(jsonList);
        Collections.reverse(reversed);

        List<Message> messages = new ArrayList<>();
        for (String json : reversed) {
            JSONObject obj = JSONUtil.parseObj(json);
            String role = obj.getStr("role");
            String content = obj.getStr("content");
            if ("user".equals(role)) {
                messages.add(new UserMessage(content));
            } else if ("assistant".equals(role)) {
                messages.add(new AssistantMessage(content));
            }
        }

        log.debug("构建上下文: sessionId={}, 消息数={}", sessionId, messages.size());
        return messages;
    }

    @Override
    public void saveToContext(Long sessionId, String role, String content) {
        String key = CONTEXT_KEY_PREFIX + sessionId;
        JSONObject msgObj = new JSONObject();
        msgObj.set("role", role);
        msgObj.set("content", content);

        redisTemplate.opsForList().leftPush(key, msgObj.toString());
        redisTemplate.opsForList().trim(key, 0, MAX_CONTEXT_MESSAGES - 1);
        redisTemplate.expire(key, CONTEXT_TTL_HOURS, TimeUnit.HOURS);

        log.debug("保存上下文: sessionId={}, role={}", sessionId, role);
    }

    @Override
    public void clearContext(Long sessionId) {
        String key = CONTEXT_KEY_PREFIX + sessionId;
        Boolean deleted = redisTemplate.delete(key);
        log.info("清除上下文缓存: sessionId={}, deleted={}", sessionId, deleted);
    }
}
