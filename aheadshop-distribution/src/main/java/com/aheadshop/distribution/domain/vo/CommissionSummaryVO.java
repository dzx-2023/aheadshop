package com.aheadshop.distribution.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionSummaryVO {

    private BigDecimal totalCommission;
    private BigDecimal availableCommission;
    private BigDecimal frozenCommission;
}
