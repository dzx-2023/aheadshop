package com.aheadshop.pay.service;

import com.aheadshop.pay.domain.dto.PayCreateDTO;
import com.aheadshop.pay.domain.dto.RefundDTO;
import com.aheadshop.pay.domain.vo.PaySubmitVO;

import java.util.Map;

public interface IPayService {

    PaySubmitVO createPay(Long userId, PayCreateDTO dto);

    Integer queryPayStatus(String payNo);

    String handleAlipayCallback(Map<String, String> params);

    void refund(RefundDTO dto);

    String handleRefundCallback(Map<String, String> params);
}
