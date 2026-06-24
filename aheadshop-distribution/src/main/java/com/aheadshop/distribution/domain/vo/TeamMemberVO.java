package com.aheadshop.distribution.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamMemberVO {

    private Long userId;
    private String nickname;
    private String avatar;
    private LocalDateTime joinTime;
}
