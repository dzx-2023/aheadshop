package com.aheadshop.order.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDTO {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    private String remark;

    /** 直接购买：指定 SKU（为空则从购物车取已勾选商品） */
    private Long skuId;

    /** 直接购买：数量 */
    private Integer quantity;
}
