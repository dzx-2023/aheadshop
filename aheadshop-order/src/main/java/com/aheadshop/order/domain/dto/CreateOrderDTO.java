package com.aheadshop.order.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDTO {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    private String remark;
}
