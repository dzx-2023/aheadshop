package com.aheadshop.order.feign;

import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "aheadshop-pay", path = "/inner/pay")
public interface PayFeignClient {

    /**
     * 通知支付服务发起支付宝退款
     *
     * @param orderNo      订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     */
    @PostMapping("/refund")
    Result<Void> refund(@RequestParam("orderNo") String orderNo,
                        @RequestParam("refundAmount") java.math.BigDecimal refundAmount,
                        @RequestParam(value = "refundReason", required = false) String refundReason);
}
