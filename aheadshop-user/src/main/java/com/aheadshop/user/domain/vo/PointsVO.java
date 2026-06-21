package com.aheadshop.user.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointsVO {

    /** 当前总积分 */
    private Integer totalPoints;

    /** 本月获得积分 */
    private Integer monthPoints;

    /** 总积分排名 */
    private Long rank;
}
