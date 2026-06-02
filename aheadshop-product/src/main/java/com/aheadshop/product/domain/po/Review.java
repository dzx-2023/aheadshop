package com.aheadshop.product.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long spuId;
    private Long skuId;
    private String orderNo;
    private Integer score;
    private String content;
    private String images;
    private Integer isAnonymous;
    private String reply;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
