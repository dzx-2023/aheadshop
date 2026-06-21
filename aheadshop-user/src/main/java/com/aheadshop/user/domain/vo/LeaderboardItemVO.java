package com.aheadshop.user.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardItemVO {

    private Integer rank;
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer points;
}
