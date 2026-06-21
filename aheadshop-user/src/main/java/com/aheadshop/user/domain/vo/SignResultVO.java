package com.aheadshop.user.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SignResultVO {

    /** 本次获得积分(基础+奖励) */
    private Integer points;

    /** 当前连续签到天数 */
    private Integer streak;

    /** 连续奖励积分 */
    private Integer bonusPoints;

    /** 总积分 */
    private Integer totalPoints;

    /** 当月已签到日期列表 */
    private List<Integer> signDays;
}
