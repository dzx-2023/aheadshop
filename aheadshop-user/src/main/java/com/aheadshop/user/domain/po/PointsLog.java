package com.aheadshop.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("points_log")
public class PointsLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 积分变动(正数获取, 负数消耗) */
    private Integer points;

    /** 类型: 1=每日签到 2=连续奖励 3=补签消耗 4=兑换消耗 5=系统赠送 */
    private Integer type;

    private String description;

    /** 业务编号(如补签日期 yyyy-MM-dd) */
    private String bizNo;

    private LocalDateTime createTime;
}
