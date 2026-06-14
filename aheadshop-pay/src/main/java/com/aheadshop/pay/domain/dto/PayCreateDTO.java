package com.aheadshop.pay.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayCreateDTO {

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    /**
     * 前端支付完成后的回跳地址（可选，不传则用配置文件默认值）
     */
    private String returnUrl;
}
