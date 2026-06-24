package com.aheadshop.distribution.task;

import com.aheadshop.distribution.service.ICommissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommissionSettleTask {

    private final ICommissionService commissionService;

    /**
     * 每天凌晨 2 点执行：将超过保护期的冻结佣金转为可用
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void settleCommission() {
        log.info("开始执行佣金解冻定时任务...");
        try {
            commissionService.settleCommission();
            log.info("佣金解冻定时任务完成");
        } catch (Exception e) {
            log.error("佣金解冻定时任务异常", e);
        }
    }
}
