package com.aheadshop.product.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewVO {

    private Long id;
    private Long userId;
    private String nickname;
    private String avatar;
    private Long spuId;
    private Long skuId;
    private Long orderId;
    private Integer score;
    private String content;
    private String images;
    private Integer isAnonymous;
    private String reply;
    private LocalDateTime createTime;
}
