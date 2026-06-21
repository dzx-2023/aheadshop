package com.aheadshop.user.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PointsLogVO {

    private Long id;
    private Integer points;
    private Integer type;
    private String typeName;
    private String description;
    private LocalDateTime createTime;
}
