package com.aheadshop.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayType {

    ALIPAY(1, "支付宝"),
    WECHAT(2, "微信支付");

    private final int code;
    private final String desc;
}