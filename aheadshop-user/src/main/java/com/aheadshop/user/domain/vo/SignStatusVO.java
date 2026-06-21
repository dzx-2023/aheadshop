package com.aheadshop.user.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SignStatusVO {

    private Integer year;
    private Integer month;

    /** 当月已签到的日期列表(1~31) */
    private List<Integer> signDays;

    /** 本月签到总天数 */
    private Integer totalDays;

    /** 本月已补签次数 */
    private Integer retroUsed;

    /** 本月补签上限 */
    private Integer retroLimit;

    /** 今日是否已签到 */
    private Boolean todaySigned;
}
