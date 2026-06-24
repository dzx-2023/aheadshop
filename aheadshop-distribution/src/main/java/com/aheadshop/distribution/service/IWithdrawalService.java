package com.aheadshop.distribution.service;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.distribution.domain.dto.WithdrawApplyDTO;
import com.aheadshop.distribution.domain.dto.WithdrawAuditDTO;
import com.aheadshop.distribution.domain.po.WithdrawalRecord;

public interface IWithdrawalService {

    /**
     * 申请提现
     */
    void apply(Long userId, WithdrawApplyDTO dto);

    /**
     * 管理员审核提现
     */
    void audit(WithdrawAuditDTO dto);

    /**
     * 确认打款（APPROVED → PAID）
     */
    void confirmPaid(Long id);

    /**
     * 获取提现记录（支持状态筛选）
     */
    PageResult<WithdrawalRecord> getList(Long userId, Integer status, Integer pageNum, Integer pageSize);
}
