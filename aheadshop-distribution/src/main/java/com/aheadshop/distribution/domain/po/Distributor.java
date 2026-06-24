package com.aheadshop.distribution.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("distributor")
public class Distributor extends BaseEntity {

    private Long userId;
    private String inviteCode;
    private Integer level;
    private BigDecimal totalCommission;
    private BigDecimal availableCommission;
    private BigDecimal frozenCommission;
    private Integer totalTeamCount;
    private Integer status;
}
