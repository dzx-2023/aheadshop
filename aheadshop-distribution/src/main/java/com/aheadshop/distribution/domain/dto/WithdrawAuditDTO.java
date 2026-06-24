package com.aheadshop.distribution.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawAuditDTO {

    @NotNull
    private Long id;

    @NotNull
    private Boolean approved;

    private String remark;
}
