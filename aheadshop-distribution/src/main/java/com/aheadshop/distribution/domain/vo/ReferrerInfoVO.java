package com.aheadshop.distribution.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReferrerInfoVO {

    /** 推荐人用户ID */
    private Long userId;

    /** 推荐人昵称 */
    private String nickname;

    /** 推荐人头像 */
    private String avatar;

    /** 绑定时使用的邀请码 */
    private String inviteCode;

    /** 绑定时间 */
    private LocalDateTime bindTime;
}
