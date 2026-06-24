package com.aheadshop.distribution.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("referral_relation")
public class ReferralRelation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long referrerId;
    private String inviteCode;
    private LocalDateTime createTime;
}
