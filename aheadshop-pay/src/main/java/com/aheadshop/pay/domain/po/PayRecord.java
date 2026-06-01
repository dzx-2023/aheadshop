package com.aheadshop.pay.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pay_record")
public class PayRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String payNo;
    private String orderNo;
    private Long userId;
    private BigDecimal payAmount;
    private Integer payType;
    private String tradeNo;
    private Integer status;
    private LocalDateTime payTime;
    private LocalDateTime callbackTime;
    private String callbackContent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
