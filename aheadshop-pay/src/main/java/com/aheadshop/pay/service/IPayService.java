package com.aheadshop.pay.service;

import com.aheadshop.pay.domain.dto.PayCreateDTO;
import com.aheadshop.pay.domain.vo.PaySubmitVO;

public interface IPayService {

    PaySubmitVO createPay(Long userId, PayCreateDTO dto);

    Integer queryPayStatus(String payNo);

    String handleAlipayCallback(java.util.Map<String, String> params);
}
