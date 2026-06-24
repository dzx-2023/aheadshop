package com.aheadshop.distribution.service;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.distribution.domain.po.CommissionRecord;
import com.aheadshop.distribution.domain.vo.CommissionSummaryVO;

import java.math.BigDecimal;

public interface ICommissionService {

    /**
     * 计算佣金（订单支付后 MQ 触发）
     */
    void calculateCommission(String orderNo, Long buyerId, BigDecimal payAmount);

    /**
     * 取消佣金（退款时调用）
     */
    void cancelCommission(String orderNo);

    /**
     * 获取佣金汇总
     */
    CommissionSummaryVO getSummary(Long userId);

    /**
     * 定时解冻佣金（冻结超过保护期 → 可用）
     */
    void settleCommission();

    /**
     * 佣金明细列表（分页+状态筛选）
     */
    PageResult<CommissionRecord> getList(Long userId, Integer status, Integer pageNum, Integer pageSize);
}
