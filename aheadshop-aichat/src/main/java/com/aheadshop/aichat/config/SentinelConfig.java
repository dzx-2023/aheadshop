package com.aheadshop.aichat.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 熔断降级配置
 *
 * <p>规则：
 * <ul>
 *   <li>资源名 llmChat / llmStreamChat</li>
 *   <li>慢调用比例模式：RT > 10000ms 触发</li>
 *   <li>异常比例模式：异常比例 > 50% 触发</li>
 *   <li>熔断后 30 秒半开</li>
 * </ul>
 */
@Slf4j
@Configuration
public class SentinelConfig {

    @PostConstruct
    public void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // 同步 LLM 调用熔断规则
        DegradeRule llmChatRule = new DegradeRule();
        llmChatRule.setResource("llmChat");
        llmChatRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        llmChatRule.setCount(0.5);           // 异常比例 > 50% 触发熔断
        llmChatRule.setSlowRatioThreshold(-1);
        llmChatRule.setTimeWindow(30);       // 熔断持续 30 秒
        llmChatRule.setMinRequestAmount(5);  // 最少 5 次请求才统计
        llmChatRule.setStatIntervalMs(60000); // 统计窗口 60 秒
        rules.add(llmChatRule);

        // 流式 LLM 调用熔断规则
        DegradeRule llmStreamRule = new DegradeRule();
        llmStreamRule.setResource("llmStreamChat");
        llmStreamRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        llmStreamRule.setCount(0.5);
        llmStreamRule.setSlowRatioThreshold(-1);
        llmStreamRule.setTimeWindow(30);
        llmStreamRule.setMinRequestAmount(5);
        llmStreamRule.setStatIntervalMs(60000);
        rules.add(llmStreamRule);

        DegradeRuleManager.loadRules(rules);
        log.info("Sentinel 熔断规则初始化完成: {}", rules);
    }
}
