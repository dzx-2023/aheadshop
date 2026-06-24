package com.aheadshop.distribution.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawApplyDTO {

    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "100.00", message = "最低提现金额100元")
    private BigDecimal amount;
}
