package com.aheadshop.order.service;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.order.domain.vo.RefundPageVO;

public interface IRefundService {

    void createRefund(Long userId, String orderNo, Integer refundType, String reason);

    void auditRefund(Long refundId, boolean approved, String remark);

    PageResult<RefundPageVO> listRefunds(Integer status, Integer pageNum, Integer pageSize);

    PageResult<RefundPageVO> listUserRefunds(Long userId, Integer status, Integer pageNum, Integer pageSize);

    RefundPageVO getRefundByOrderNo(String orderNo);
}
