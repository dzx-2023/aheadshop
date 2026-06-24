package com.aheadshop.distribution.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DistributionInfoVO {

    private Long userId;
    private String inviteCode;
    private Integer level;
    private BigDecimal totalCommission;
    private BigDecimal availableCommission;
    private BigDecimal frozenCommission;
    private Integer totalTeamCount;
    private Integer status;
}
