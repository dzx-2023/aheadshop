package com.aheadshop.order.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_log")
public class OrderLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private String operator;
    private Integer statusBefore;
    private Integer statusAfter;
    private String note;
    private LocalDateTime createTime;
}
