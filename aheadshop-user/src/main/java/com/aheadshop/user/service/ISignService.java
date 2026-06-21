package com.aheadshop.user.service;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.user.domain.vo.*;

import java.util.List;

public interface ISignService {

    /** 每日签到 */
    SignResultVO checkIn(Long userId);

    /** 补签指定日期 */
    void retroSign(Long userId, String date);

    /** 当月签到状态(日历用) */
    SignStatusVO getMonthStatus(Long userId, Integer year, Integer month);

    /** 当前连续签到天数 */
    Integer getStreak(Long userId);

    /** 我的积分信息 */
    PointsVO getPoints(Long userId);

    /** 积分流水(分页) */
    PageResult<PointsLogVO> getPointLog(Long userId, Integer pageNum, Integer pageSize);

    /** 积分排行榜 */
    List<LeaderboardItemVO> getLeaderboard(String type);
}
