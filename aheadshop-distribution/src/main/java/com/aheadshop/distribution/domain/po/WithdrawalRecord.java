package com.aheadshop.distribution.domain.po;

import com.aheadshop.common.mybatis.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("withdrawal_record")
public class WithdrawalRecord extends BaseEntity {

    private Long distributorId;
    private BigDecimal amount;
    private Integer status;
    private Long auditUserId;
    private LocalDateTime auditTime;
    private LocalDateTime payTime;
    private String remark;
}
