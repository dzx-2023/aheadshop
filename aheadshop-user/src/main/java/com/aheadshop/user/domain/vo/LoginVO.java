package com.aheadshop.user.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {
    private Long userId;
    private String username;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}