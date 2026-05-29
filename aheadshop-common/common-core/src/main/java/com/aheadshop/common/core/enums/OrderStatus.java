package com.aheadshop.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING_PAY(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    RECEIVED(3, "已收货"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    REFUNDING(6, "退款中"),
    REFUNDED(7, "已退款");

    private final int code;
    private final String desc;

    public static OrderStatus of(int code) {
        for (OrderStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知的订单状态: " + code);
    }
}